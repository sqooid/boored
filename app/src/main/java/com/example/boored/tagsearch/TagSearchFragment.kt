package com.example.boored.tagsearch

import android.app.SearchManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.transition.TransitionInflater
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.loader.app.LoaderManager
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.boored.R
import com.example.boored.databinding.TagSearchFragmentBinding
import com.example.boored.util.Constants
import com.example.boored.util.PostScrollAdapter
import org.w3c.dom.Text
import retrofit2.HttpException

class TagSearchFragment : Fragment() {

    companion object {
        fun newInstance() = TagSearchFragment()
    }

    private lateinit var viewModel: TagSearchViewModel
    private lateinit var binding: TagSearchFragmentBinding
    private lateinit var windowManager: WindowManager

    // Getting preferences
    private lateinit var preferences: SharedPreferences

    // Keeping track of current source server
    private  var server: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /**
         * Setting transitions
         */
        val transitionInflater = TransitionInflater.from(requireContext())
        exitTransition = transitionInflater.inflateTransition(R.transition.fade_transition)
        enterTransition = transitionInflater.inflateTransition(R.transition.fade_transition)

        // Data Binding
        binding = TagSearchFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        // Defining window manager to get width of screen to be passed to recycler view layout manager
        windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // ViewModel bound
        val activity = requireNotNull(this.activity)
        viewModel = ViewModelProvider(this, TagSearchViewModel.viewModelFactory(activity.application)).get(TagSearchViewModel::class.java)
        binding.viewModel = viewModel

        // Search field
        setHasOptionsMenu(true)

        // Calling function to set up recycler view
        preferences = requireContext().getSharedPreferences(Constants.PREFERENCE_NAME, 0)

        bindRecyclerView()

        /**
         * Observers
         */
        viewModel.postSuccess.observe(viewLifecycleOwner, Observer {
            if (!it) {
                Toast.makeText(activity, "No posts with those tags", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.searchParameters.observe(viewLifecycleOwner, Observer {
            updateToolbarTitle(it)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = TagSearchFragmentArgs.fromBundle(requireArguments())
        if (args.linkedTag != "") {
            server = preferences.getString("server", "0")!!.toInt()
            viewModel.doInitialSearchWithTags(server, args.linkedTag)
        }
    }

    /**
     * Helper functions
     */
    private fun updateToolbarTitle(text: String) {
        val toolBar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolBar?.title = text
        lastQuery = text
    }

    var lastQuery = ""


    /**
     * Options menu (containing searchView) creation and configuration
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tag_search_options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        /**
         * Search field in toolbar
         */
        val searchItem = menu.findItem(R.id.tag_search_field)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = searchItem?.actionView as SearchView


        with(searchView) {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))

            /**
             * Putting previous query into search box
             */
            setOnSearchClickListener {
                if (lastQuery != "") {
                    searchView.setQuery(lastQuery, false)
                }
            }

            /**
             * Setting text change and submit listener
             */
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                /**
                 * Text submit listener
                 */
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchItem.collapseActionView()
                    if (query != null) {
                        server = preferences.getString("server", "0")!!.toInt()
                        viewModel.doInitialSearchWithTags(server, query)
                        Log.i("Test", "Search made")
                    }
                    val inputMethodManager =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
                    return true
                }

                /**
                 * Text change listener
                 */
                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })

            /**
             * Setting focus change listener
             */
            setOnQueryTextFocusChangeListener { view, hasFocus ->
                val inputMethodManager =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (hasFocus) {
                    inputMethodManager.showSoftInput(view, 0)
                } else {
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
    }

    /**
     * Changing colour of server menu items to black
     */
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        for (i in 1..2) {
            val menuItem = menu.getItem(i)
            val title = menuItem.title.toString()
            val newTitle = SpannableString(title)
            newTitle.setSpan(ForegroundColorSpan(Color.BLACK), 0, newTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            menuItem.title = newTitle
        }
    }

    /**
     * Click handlers for changing source server
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.danbooru_menu_option -> preferences.edit().putString("server", Constants.DANBOORU.toString()).apply()
            R.id.gelbooru_server_option -> preferences.edit().putString("server", Constants.GELBOORU.toString()).apply()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Function to set up recycler view with staggered grid layout
     */
    private fun bindRecyclerView() {
        /**
         * Getting width of device in order to scale image to edges
         */
        val width: Int
        width = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.x
        } else {
            windowManager.currentWindowMetrics.bounds.width()
        }
        val columns = preferences.getInt("scroll_columns_tag_search", 2)
        val lowResMode = preferences.getBoolean("use_preview_tag_search", false)

        /**
         * Instantiating adapter
         */
        val adapter = PostScrollAdapter(columns, width, PostScrollAdapter.OnClickListener { post, postNumber ->
            this.findNavController()
                .navigate(TagSearchFragmentDirections.tagSearchShowDetailed(post, postNumber))
        }, lowResMode)
        binding.tagSearchRecyclerView.adapter = adapter

        /**
         * Creating staggered grid layout manager
         */
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(columns, LinearLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.tagSearchRecyclerView.layoutManager = staggeredGridLayoutManager

        /**
         * Detecting when scrolling has neared end of retrieved posts
         */
        adapter.postsExhausted.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.getMorePosts(server)
                adapter.doneGettingPosts()
            }
        })
    }
}

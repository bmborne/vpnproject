package com.witvpn.ikev2.presentation.ui.servers

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.witvpn.ikev2.R
import com.witvpn.ikev2.databinding.FragmentServersBinding
import com.witvpn.ikev2.domain.model.Server
import com.witvpn.ikev2.domain.model.Status
import com.witvpn.ikev2.presentation.base.BaseFragment
import com.witvpn.ikev2.presentation.ui.MainTabUIDelegate
import com.witvpn.ikev2.presentation.ui.ShareViewModel
import com.witvpn.ikev2.presentation.ui.servers.tab.TabFragment
import com.witvpn.ikev2.presentation.utils.*
import com.witvpn.ikev2.presentation.utils.Feature.IS_ADMIN
import com.witvpn.ikev2.presentation.widget.bottomnav.BottomNavBar
import dagger.hilt.android.AndroidEntryPoint

interface ServersUIDelegate {
    fun handleServerClicked(server: Server?)
}

@AndroidEntryPoint
class ServersFragment : BaseFragment<FragmentServersBinding>(R.layout.fragment_servers), ServersUIDelegate {

    private val viewModel: ServersViewModel by activityViewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()

    private val tabs = listOf(
        TabFragment.newInstance(TabFragment.ALL_LOCATION),
        TabFragment.newInstance(TabFragment.RECOMMENDED)
    )

    override fun initBinding(view: View): FragmentServersBinding {
        return FragmentServersBinding.bind(view)
    }

    override fun initView() {

        binding.btnLeft.setOnClickListener {
            FragmentUtils.getParent(this, MainTabUIDelegate::class.java)?.setCurrentTab(BottomNavBar.TAB_HOME)
        }

        binding.viewPager.apply {
            adapter = ViewPagerAdapter(childFragmentManager)
        }
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.setLetterSpacing(0.12f)
    }

    override fun initObserve() {
        shareViewModel.userLiveData.value?.let { user ->
            viewModel.execute(user)
        }

        viewModel.getServers().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> binding.progress.visibility = View.VISIBLE
                else -> {
                    binding.progress.visibility = View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.updateColorStatusBar(R.color.colorNavBottomBackground)
    }

    override fun onPause() {
        super.onPause()
        activity?.updateColorStatusBar(R.color.colorPrimary)
    }

    inner class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return tabs[position]
        }

        override fun getCount(): Int {
            return tabs.count()
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) context?.getString(R.string.tab_all_location) else context?.getString(
                R.string.tab_recommended
            )
        }
    }

    override fun handleServerClicked(server: Server?) {

        if (!shareViewModel.isPremium && server?.premium == true && !IS_ADMIN) {
            FragmentUtils.getParent(this, MainTabUIDelegate::class.java)?.setCurrentTab(BottomNavBar.TAB_PREMIUM)
            return
        }

        FragmentUtils.getParent(this, MainTabUIDelegate::class.java)?.setServerToConnect(server)
    }
}
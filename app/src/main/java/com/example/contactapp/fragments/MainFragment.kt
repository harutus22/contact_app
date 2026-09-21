package com.example.contactapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactapp.repository.ContactsRepository
import com.example.contactapp.R
import com.example.contactapp.data_models.Contact
import com.example.contactapp.databinding.FragmentMainBinding
import com.example.contactapp.recycler_view.ContactRecyclerViewAdapter
import com.example.contactapp.view_models.ContactsViewModel
import com.example.contactapp.view_models.ContactsViewModelFactory
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private lateinit var adapter: ContactRecyclerViewAdapter

    private val viewModel: ContactsViewModel by viewModels {
        ContactsViewModelFactory(
            ContactsRepository(requireContext().applicationContext)
        )
    }

    private fun checkContactsPermission() {
        if (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.loadContacts()
        } else {
            requestContactsPermission()
        }
    }

    private fun requestContactsPermission() {
        permissionLauncher.launch(
            Manifest.permission.READ_CONTACTS
        )
    }

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                viewModel.loadContacts()
            } else {
                _binding?.errorText?.isVisible = true
                _binding?.errorText?.text =
                    getString(R.string.contacts_permission_is_required)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        initAdapter()
        observeViewModel()
        checkContactsPermission()
    }

    private fun initAdapter() {
        adapter = ContactRecyclerViewAdapter { contact ->
            openDetailedFragment(contact)
        }
        _binding?.recyclerView?.layoutManager =
            LinearLayoutManager(requireContext())
        _binding?.recyclerView?.adapter = adapter
        _binding?.recyclerView?.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.uiState.collect { state ->

                    _binding?.progressBar?.isVisible =
                        state.isLoading

                    _binding?.errorText?.isVisible =
                        state.error != null

                    _binding?.errorText?.text =
                        state.error

                    adapter.submitList(state.contacts)
                }
            }
        }
    }

    private fun openDetailedFragment(contact: Contact) {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.main, DetailedFragment.newInstance(contact))
            addToBackStack(null)
            commit()
        }
    }
}
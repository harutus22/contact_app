package com.example.contactapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.contactapp.R
import com.example.contactapp.data_models.Contact
import com.example.contactapp.databinding.FragmentDetailedBinding


private const val ARG_CONTACT = "contact"

class DetailedFragment : Fragment() {
    private var contact: Contact? = null
    private var _binding: FragmentDetailedBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable(ARG_CONTACT, Contact::class.java)
            } else {
                arguments?.getParcelable(ARG_CONTACT)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailedBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailedBinding.bind(view)
        _binding?.toolbar?.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        initViews()
    }

    private fun initViews() {
        _binding?.image?.setImageURI(contact?.photoUri?.toUri())
        _binding?.name?.text = contact?.name

        initNumbers()
        initMails()
        initAddresses()

        if (contact?.organization != null) {
            _binding?.organizationLayout?.visibility = View.VISIBLE
            _binding?.organizationName?.text = contact?.organization.toString()
        }

        if (contact?.birthday != null) {
            _binding?.birthdayLayout?.visibility = View.VISIBLE
            _binding?.birthday?.text = contact?.birthday
        }
    }

    private fun initAddresses() {
        if (contact?.homeAddresses?.isNotEmpty() == true || contact?.workAddresses?.isNotEmpty() == true) {
            _binding?.addressesLayout?.visibility = View.VISIBLE
            setViewsData(
                _binding?.homeAddress!!,
                R.string.home_subtitle,
                *(contact?.homeAddresses?.toTypedArray() ?: emptyArray())
            )
            setViewsData(
                _binding?.workAddress!!,
                R.string.work_subtitle,
                contact?.workAddresses?.toString()
            )
        }
    }

    private fun initMails() {
        if (contact?.homeEmails?.isNotEmpty() == true || contact?.workEmails?.isNotEmpty() == true) {
            _binding?.emailsLayout?.visibility = View.VISIBLE
            setViewsData(
                _binding?.homeEmail!!,
                R.string.home_subtitle,
                *(contact?.homeEmails?.toTypedArray() ?: emptyArray())
            )
            setViewsData(
                _binding?.workEmail!!,
                R.string.work_subtitle,
                *(contact?.workEmails?.toTypedArray() ?: emptyArray())
            )
        }
    }

    private fun initNumbers() {
        if (contact?.mobilePhones?.isNotEmpty() == true || contact?.homePhones?.isNotEmpty() == true
            || contact?.workPhones?.isNotEmpty() == true
        ) {
            _binding?.phoneNumbersLayout?.visibility = View.VISIBLE
            setViewsData(
                _binding?.mobilePhone!!,
                R.string.mobile_subtitle,
                *(contact?.mobilePhones?.toTypedArray() ?: emptyArray())
            )
            setViewsData(
                _binding?.homePhone!!,
                R.string.home_subtitle,
                *(contact?.homePhones?.toTypedArray() ?: emptyArray())
            )
            setViewsData(
                _binding?.workPhone!!,
                R.string.work_subtitle,
                *(contact?.workPhones?.toTypedArray() ?: emptyArray())
            )
            _binding?.mobilePhone?.setOnClickListener {
                callNumber(contact?.mobilePhones.toString())
            }
            _binding?.homePhone?.setOnClickListener {
                callNumber(contact?.homePhones.toString())
            }
            _binding?.workPhone?.setOnClickListener {
                callNumber(contact?.workPhones.toString())
            }
        }
    }

    private fun setViewsData(view: TextView, preText: Int, vararg data: String?) {
        if (data.isNotEmpty())
            view.let {
                it.visibility = View.VISIBLE
                it.text = getString(preText, data.joinToString(", "))
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(contact: Contact) =
            DetailedFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CONTACT, contact)
                }
            }
    }

    private fun callNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = getString(R.string.tel, number).toUri()
        startActivity(intent)
    }
}
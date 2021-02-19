package com.hyperliteapps.pricechecker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.hyperliteapps.pricechecker.adapters.CartListAdapter
import com.hyperliteapps.pricechecker.adapters.SwipeToDeleteCallback
import com.hyperliteapps.pricechecker.databinding.ActivityMainBinding
import com.hyperliteapps.pricechecker.models.ActionType
import com.hyperliteapps.pricechecker.viewModels.ShoppingListViewModel
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class ShoppingListActivity : AppCompatActivity() {
    private val shoppingListViewModel by inject<ShoppingListViewModel> {parametersOf(this)}
    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            lifecycleOwner = this@ShoppingListActivity
            viewModel = shoppingListViewModel
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.title = "Jungsoo’s Market"

        val listAdapter = CartListAdapter()
        binding.adapter = listAdapter

        //create swipe handler to support swipe deletion
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                shoppingListViewModel.removeFromCart(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.shoppingList)

        //observe shopping cart value to determine when to update adapter and show list/no items
        shoppingListViewModel.cart.observe(this, Observer {
            it.let {
                Log.v("Activity", "inside cart observer")
                listAdapter.updateCart(it)
                if(it.isEmpty()){
                    binding.shoppingList.visibility = View.GONE
                    binding.llNoItems.visibility = View.VISIBLE
                }
                else{
                    binding.shoppingList.visibility = View.VISIBLE
                    binding.llNoItems.visibility = View.GONE
                }
            }
        })

        //observe the cart action value to determine when to show add/remove snackbars
        shoppingListViewModel.cartAction.observe(this, Observer {
            when(it) {
                ActionType.Add -> {
                    Snackbar.make(binding.root, "Item Added to Cart", Snackbar.LENGTH_SHORT).show()
                }
                ActionType.Remove -> {
                    val snackbar = Snackbar.make(binding.root, "Item Added to Cart", Snackbar.LENGTH_LONG)
                    snackbar.setAction("UNDO") { shoppingListViewModel.undoRemoveItem() }
                    snackbar.show()
                }
                else -> {
                    //do nothing
                }
            }
        })
    }

    fun onClickScanQr(v: View) {
        val integrator = IntentIntegrator(this)

        integrator.setOrientationLocked(false)
        integrator.setPrompt("Scan item QR code")
        integrator.setBeepEnabled(true)

        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)

        integrator.initiateScan()
    }

    fun onClickEnterId(v: View) {
        //show dialog with edit text for id
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Add Item")
        alertDialog.setMessage("Enter Item Id")
        val input = EditText(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        alertDialog.setView(input)

        alertDialog.setPositiveButton("Add") { dialog, _ ->  }
        alertDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        val dialog = alertDialog.create()
        dialog.show()

        // Overriding the that button here immediately handle the user's activity.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (input.text.isEmpty()) {
                input.error = "Please enter item Id"
                input.requestFocus()
            } else if(!shoppingListViewModel.addToCart(input.text.toString())){
                Toast.makeText(this, "Could not find item with that Id", Toast.LENGTH_SHORT).show()
            } else {
                dialog.dismiss()
            }
        }

    }

    //override method to get result from qr scanner
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled Scan", Toast.LENGTH_LONG).show()
            } else {
                shoppingListViewModel.addToCart(result.contents)
            }
        }
    }
}
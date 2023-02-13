package com.kaizm.ecommerce_app.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.view.Gravity
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.kaizm.ecommerce_app.model.Cart
import com.kaizm.ecommerce_app.model.Product
import com.kaizm.ecommerce_app.repository.IGetDataListener
import com.kaizm.ecommerce_app.repository.ProductRepository
import java.util.concurrent.TimeUnit

class ProductViewModel : ViewModel() {

    private val productRepository = ProductRepository()

    sealed class Status(){
        object Done : Status()
        object NotDone : Status()
    }

    private val _productFirstLiveData = MutableLiveData<List<Product>>()
    val productFirstLiveData: LiveData<List<Product>>
        get() = _productFirstLiveData

    private val _productSecondLiveData = MutableLiveData<List<Product>>()
    val productSecondLiveData: LiveData<List<Product>>
        get() = _productSecondLiveData

    private val _productThirdLiveData = MutableLiveData<List<Product>>()
    val productThirdLiveData: LiveData<List<Product>>
        get() = _productThirdLiveData

    private val _productListLiveData = MutableLiveData<List<Product>>()
    val productListLiveData: LiveData<List<Product>>
        get() = _productListLiveData

    private val _productLiveData = MutableLiveData<Product?>()
    val productLiveData: MutableLiveData<Product?>
        get() = _productLiveData

    private val _uploadProgressLiveData = MutableLiveData<Double>()
    val uploadProgressLiveData: LiveData<Double>
        get() = _uploadProgressLiveData

    private val _uploadStatusLiveData = MutableLiveData<Status>()
    val uploadStatusLiveData: LiveData<Status>
        get() = _uploadStatusLiveData

    fun setFirstData(query: Query) {
        productRepository.readData(query, object : IGetDataListener {
            override fun onGetSuccess(any: Any) {
                @Suppress("UNCHECKED_CAST")
                _productFirstLiveData.postValue(any as List<Product>)
            }
        })
    }

    fun setSecondData(query: Query) {
        productRepository.readData(query, object : IGetDataListener {
            override fun onGetSuccess(any: Any) {
                @Suppress("UNCHECKED_CAST")
                _productSecondLiveData.postValue(any as List<Product>?)
            }
        })
    }

    fun setThirdData(query: Query) {
        productRepository.readData(query, object : IGetDataListener {
            override fun onGetSuccess(any: Any) {
                @Suppress("UNCHECKED_CAST")
                _productThirdLiveData.postValue(any as List<Product>?)
            }
        })
    }

    fun setListData(query: Query) {
        productRepository.readData(query, object : IGetDataListener {
            override fun onGetSuccess(any: Any) {
                @Suppress("UNCHECKED_CAST")
                _productListLiveData.postValue(any as List<Product>?)
            }
        })
    }

    fun getProductByID(key:String){
        productRepository.readData(FirebaseDatabase.getInstance().getReference("products"), object :IGetDataListener{
            override fun onGetSuccess(any: Any) {
                @Suppress("UNCHECKED_CAST")
                val list : List<Product> = any as List<Product>
                for(i in list){
                    if(i.id == key){
                        _productLiveData.postValue(i)
                    }
                }
            }
        })
    }

    fun filterProductByCategory(category: String){
        productRepository.readData(FirebaseDatabase.getInstance().getReference("products"), object : IGetDataListener{
            override fun onGetSuccess(any: Any) {
                @Suppress("UNCHECKED_CAST")
                val anyCast : List<Product> = any as List<Product>
                val list : MutableList<Product> = mutableListOf()
                for(i in anyCast){
                    if(i.category == category){
                        list.add(i)
                    }
                }
                _productListLiveData.postValue(list)
            }
        })
    }

    fun filterProductByPrice(state:Boolean){
        productRepository.readData(FirebaseDatabase.getInstance().getReference("products").orderByChild("price"), object : IGetDataListener{
            override fun onGetSuccess(any: Any) {
                @Suppress("UNCHECKED_CAST")
                val anyCast : List<Product> = any as List<Product>
                val tempList : MutableList<Product> = mutableListOf()
                for(i in anyCast){
                    if (state){
                        tempList.add(i)
                    }else{
                        tempList.add(0,i)
                    }
                }
                _productListLiveData.postValue(tempList)
            }
        })
    }

    fun filterProductBySearch(search:String){
        productRepository.readData(FirebaseDatabase.getInstance().getReference("products"), object : IGetDataListener{
            override fun onGetSuccess(any: Any) {
                @Suppress("UNCHECKED_CAST")
                val anyCast : List<Product> = any as List<Product>
                val list : MutableList<Product> = mutableListOf()
                for(i in anyCast){
                    if(i.name!!.lowercase().contains(search.lowercase())){
                        list.add(i)
                    }
                }
                _productListLiveData.postValue(list)
            }
        })
    }


    fun clearCurrentProduct(){
        _productLiveData.value = null
    }

    fun addToCart(context: Context, product: Product, quantity: Int?) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user!!.uid
        val id: String = product.id!!
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.getReference("cart")
        val cart = quantity?.let { Cart(id, product.image, product.name, product.price, it) }
        databaseReference.child(userId).child(id).setValue(cart)

        val toast = Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

    fun deleteProduct(context: Context, product: Product){
        val databaseReference = FirebaseDatabase.getInstance().getReference("products")
        databaseReference.child(product.id!!).removeValue { error, ref ->
            Toast.makeText(context, "Xoá thành công", Toast.LENGTH_SHORT).show()
        }
    }

    fun uploadProduct(context: Context,imgUri:Uri,name:String, price:Int, category: String,quantity:Int,description:String){
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("products")
        val productID: String = databaseReference.push().key!!
        val timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

        val storageReference: StorageReference = Firebase.storage.reference.child(System.currentTimeMillis().toString() + "." + getFileExtension(context,imgUri))
        storageReference.putFile(imgUri).addOnSuccessListener(OnSuccessListener<Any?> {
            storageReference.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                val product = Product(productID, uri.toString(), name, price, timeStamp, category, quantity, description)
                databaseReference.child(productID).setValue(product)
                _uploadProgressLiveData.postValue(0.0)
                _uploadStatusLiveData.postValue(Status.Done)
                Toast.makeText(context, "Upload thành công", Toast.LENGTH_LONG).show()
            })
        }).addOnProgressListener {
            val process: Double = 100.0 * it.bytesTransferred / it.totalByteCount
            _uploadProgressLiveData.postValue(process)
            _uploadStatusLiveData.postValue(Status.NotDone)
        }.addOnFailureListener(OnFailureListener {
            Toast.makeText(
                context,
                "Upload lỗi",
                Toast.LENGTH_LONG
            ).show()
            _uploadStatusLiveData.postValue(Status.Done)
        })
    }

    fun updateProduct(context: Context,key:String,imgUri:Uri?,name:String, price:Int, category: String,quantity:Int,description:String){
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("products")

        val timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

        if(imgUri != null){
            val storageReference: StorageReference = Firebase.storage.reference.child(System.currentTimeMillis().toString() + "." + getFileExtension(context,imgUri))
            storageReference.putFile(imgUri).addOnSuccessListener(OnSuccessListener<Any?> {
                storageReference.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri> { uri ->

                    val product = Product(key, uri.toString(), name, price, timeStamp, category, quantity, description)

                    update(databaseReference,context,key,product.updateProduct())

                    _uploadProgressLiveData.postValue(0.0)
                    _uploadStatusLiveData.postValue(Status.Done)
                    Toast.makeText(context, "Update thành công", Toast.LENGTH_LONG).show()
                })
            }).addOnProgressListener {
                val process: Double = 100.0 * it.bytesTransferred / it.totalByteCount
                _uploadProgressLiveData.postValue(process)
                _uploadStatusLiveData.postValue(Status.NotDone)
            }.addOnFailureListener(OnFailureListener {
                Toast.makeText(
                    context,
                    "Update lỗi",
                    Toast.LENGTH_LONG
                ).show()
                _uploadStatusLiveData.postValue(Status.Done)
            })
        }else{
            val product = Product(key, null, name, price, timeStamp, category, quantity, description)
            update(databaseReference,context,key,product.updateProductWithoutImg())
        }

    }

    private fun update(databaseReference : DatabaseReference,context: Context, key:String, map: Map<String,Any?>){
        databaseReference.child(key).updateChildren(map,object : DatabaseReference.CompletionListener{
            override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                _uploadProgressLiveData.postValue(0.0)
                _uploadStatusLiveData.postValue(Status.Done)
                Toast.makeText(context, "Update thành công", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getFileExtension(context: Context ,imgUri: Uri): String? {
        val contentResolver: ContentResolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imgUri))
    }



}
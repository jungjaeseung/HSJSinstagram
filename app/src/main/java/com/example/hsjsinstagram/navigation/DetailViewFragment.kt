package com.example.hsjsinstagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hsjsinstagram.R
import com.example.hsjsinstagram.navigation.Model.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment : Fragment(){
    lateinit var firestore : FirebaseFirestore
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        firestore = FirebaseFirestore.getInstance()
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)

        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
        return view
    }
    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()
        init {
            firestore.collection("images").orderBy("timestamp").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as CustomViewHolder).itemView

            //UserId
            viewHolder.detailviewitem_profile_textview.text = contentDTOs!![position].userId

            //Image
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewHolder.detailviewitem_imageview_content)

            //Explain of content
            viewHolder.detailviewitem_explain_textview.text = contentDTOs!![position].explain

            //likes
            viewHolder.detailviewitem_favoritecounter_textview.text = "Likes " + contentDTOs!![position].favoriteCount

            //ProfileImage
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewHolder.detailviewitem_profile_image)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }
}
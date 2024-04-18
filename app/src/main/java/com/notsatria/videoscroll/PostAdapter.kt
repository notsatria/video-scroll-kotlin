package com.notsatria.videoscroll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class PostAdapter(options: FirebaseRecyclerOptions<Post>) :
    FirebaseRecyclerAdapter<Post, PostAdapter.VideoViewHolder>(options) {

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val video: VideoView = itemView.findViewById(R.id.videoView)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun setData(model: Post) {
            video.setVideoPath(model.videoUrl)
            title.text = model.title
            description.text = model.description

            video.setOnPreparedListener { mp ->
                progressBar.visibility = View.GONE
                mp.start()
            }

            video.setOnCompletionListener { mp ->
                mp.start()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(p0: PostAdapter.VideoViewHolder, p1: Int, p2: Post) {
        p0.setData(p2)
    }

}
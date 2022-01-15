@file:Suppress("CascadeIf")

package com.gaming.community.flexster

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gaming.community.flexster.chat.FightLogPostCommentActivity
import com.gaming.community.flexster.group.ClubFightLogCommentActivity
import com.gaming.community.flexster.group.CommentGroupActivity
import com.gaming.community.flexster.group.CreateGroupPostActivity
import com.gaming.community.flexster.group.GroupFightLogCommentActivity
import com.gaming.community.flexster.post.CommentActivity
import com.gaming.community.flexster.post.CreatePostActivity
import io.stipop.Stipop
import io.stipop.StipopDelegate
import io.stipop.extend.StipopImageView
import io.stipop.model.SPPackage
import io.stipop.model.SPSticker

class StickersPost : AppCompatActivity(), StipopDelegate {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stickers)

        val stipopIV = findViewById<StipopImageView>(R.id.stipopIV)

        Stipop.connect(this, stipopIV, "1234", "en", "US", this)

        Stipop.showSearch()

    }

    override fun onStickerSelected(sticker: SPSticker): Boolean {

        if (intent.getStringExtra("activity").equals("post")){
            val i = Intent(this, CreatePostActivity::class.java)
            i.putExtra("gif",  sticker.stickerImg.toString())
            startActivity(i)
            finish()
        }else
        if (intent.getStringExtra("activity").equals("group")){
            val i = Intent(this, CreateGroupPostActivity::class.java)
            i.putExtra("gif",  sticker.stickerImg.toString())
            startActivity(i)
            finish()
        }else
            if (intent.getStringExtra("activity").equals("comment")){
                val i = Intent(this, CommentActivity::class.java)
                i.putExtra("gif",  sticker.stickerImg.toString())
                i.putExtra("postID", intent.getStringExtra("postID"))
                startActivity(i)
                finish()
            }
            else if(intent.getStringExtra("activity").equals("fightpost"))
            {
                val i = Intent(this, FightLogPostCommentActivity::class.java)
                i.putExtra("gif",  sticker.stickerImg.toString())
                i.putExtra("postID", intent.getStringExtra("postID"))
                i.putExtra("user_id", intent.getStringExtra("user_id"))
                startActivity(i)
                finish()
            }
            else if(intent.getStringExtra("activity").equals("groupfightpost"))
            {
                val i = Intent(this, GroupFightLogCommentActivity::class.java)
                i.putExtra("gif",  sticker.stickerImg.toString())
                i.putExtra("postID", intent.getStringExtra("postID"))
                i.putExtra("group_id", intent.getStringExtra("group_id"))
                startActivity(i)
                finish()
            }
            else if(intent.getStringExtra("activity").equals("clubfightpost"))
            {
                val i = Intent(this, ClubFightLogCommentActivity::class.java)
                i.putExtra("gif",  sticker.stickerImg.toString())
                i.putExtra("postID", intent.getStringExtra("postID"))
                i.putExtra("group_id", intent.getStringExtra("group_id"))
                startActivity(i)
                finish()
            }
            else
            {
                if (intent.getStringExtra("activity").equals("groupcomment")){
                    val i = Intent(this, CommentGroupActivity::class.java)
                    i.putExtra("gif",  sticker.stickerImg.toString())
                    i.putExtra("postID", intent.getStringExtra("postID"))
                    i.putExtra("group", intent.getStringExtra("groupId"))
                    startActivity(i)
                    finish()
                }
            }


        return true
    }

    override fun canDownload(spPackage: SPPackage): Boolean {

        return true
    }

}
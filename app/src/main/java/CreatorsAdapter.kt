import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
//import androidx.activity.R
import androidx.recyclerview.widget.RecyclerView

import androidx.core.net.toUri
import com.example.recipeapp.R
import com.example.recipeapp.model.Creator


class CreatorsAdapter(private val creators: List<Creator>) :
    RecyclerView.Adapter<CreatorsAdapter.CreatorViewHolder>() {

    class CreatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById< TextView>(R.id.Name)
        val email = itemView.findViewById<TextView>(R.id.Email)
        val github = itemView.findViewById<TextView>(R.id.GitHub)
        val linkedin = itemView.findViewById<TextView>(R.id.LinkedIn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreatorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_creator, parent, false)
        return CreatorViewHolder(view)
    }

    override fun onBindViewHolder(holder: CreatorViewHolder, position: Int) {
        val creator = creators[position]
        holder.name.text = creator.name
        holder.email.text = creator.email

        holder.github.setOnClickListener {
            openLink(holder.itemView.context, creator.gitHub)
        }
        holder.linkedin.setOnClickListener {
            openLink(holder.itemView.context, creator.linkedIn)
        }
    }

    override fun getItemCount(): Int = creators.size

    private fun openLink(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }
}
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.migo.Event
import com.example.migo.R
import com.example.migo.SQLitehelper
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.migo.HomeFragmentDirections


class EventAdapter(private var eventList: List<Event>, private val loggedInUserId: Int) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val dateTimeTextView: TextView = itemView.findViewById(R.id.dateTimeTextView)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_event, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentItem = eventList[position]
        holder.titleTextView.text = currentItem.titulo
        holder.descriptionTextView.text = currentItem.descricao

        val dateTime = "Data: ${currentItem.data} às ${currentItem.horario}"
        holder.dateTimeTextView.text = dateTime

        // Decode the base64 string to a Bitmap and set it on the ImageView
        val imageBytes = Base64.decode(currentItem.imagem, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.imageView.setImageBitmap(bitmap)

        // Show or hide the buttons based on the userId
        if (currentItem.userId == loggedInUserId) {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
        } else {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }

        holder.editButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToEditEventFragment(currentItem.id)
            it.findNavController().navigate(action)
        }

        holder.deleteButton.setOnClickListener {
            val sqLitehelper = SQLitehelper(holder.itemView.context)
            val rowsAffected = sqLitehelper.deactivateEvent(currentItem.id)
            if (rowsAffected > 0) {
                Toast.makeText(holder.itemView.context, "Evento excluído com sucesso", Toast.LENGTH_LONG).show()
                // Atualizar a lista de eventos e notificar o adapter
                eventList = eventList.filter { it.id != currentItem.id }
                notifyDataSetChanged()
            } else {
                Toast.makeText(holder.itemView.context, "Erro ao excluir o evento", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = eventList.size

    fun updateEvents(newEvents: List<Event>) {
        eventList = newEvents
        notifyDataSetChanged()
    }
}

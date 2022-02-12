package io.eflamm.notlelo.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import io.eflamm.notlelo.R
import io.eflamm.notlelo.model.Event

// source : https://stackoverflow.com/questions/27461923/arrayadapter-getview-returns-nullpointerexception
class EventSpinnerAdapter(
    context: Context, textViewResourceId: Int,
    values: List<Event>
) : ArrayAdapter<Event>(context, textViewResourceId, values) {

    private val values: List<Event> = values

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): Event {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var anotherConvertView = convertView
        var holder: ViewHolder
        if(convertView == null) {
            anotherConvertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, null)
            holder = ViewHolder()
            holder.eventName = anotherConvertView.findViewById(R.id.event_name_spinner_card) as TextView
        } else {
            holder = convertView.tag as ViewHolder
        }
        holder.eventName?.text = values[position].name
        return anotherConvertView!!
    }

    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View? {

        var anotherConvertView = convertView
        var holder: ViewHolder?
        if(convertView == null) {
            anotherConvertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, null)
            holder = ViewHolder()
            holder.eventName = anotherConvertView.findViewById(R.id.event_name_spinner_card) as TextView
        } else {
            holder = convertView.tag as? ViewHolder
        }
        holder?.eventName?.text = values[position].name
        return anotherConvertView
    }


    internal class ViewHolder {
        var eventName: TextView? = null
    }

}

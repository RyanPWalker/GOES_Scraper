package com.brady.scrapermain;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**************************************************************************
 * MyAdapter
 *
 * Adapter for RecyclerView.  Purifies the response string of unneeded HTML
 * code and decodes the header.  The header contains the info for the date
 * and time.
 **************************************************************************/
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    String response;
    Context context;
    String[] titles = new String[8];    // initial array size; header display
    String[] details = new String[8];   // initial array size; raw data display
    ParsedData [] searches = new ParsedData[1]; // initial size
    int numStrings;

    // Non-Default Constructor
    MyAdapter(String response, Context context){
        this.context = context;
        this.response = response;

        //this code parses the html and puts each p tag in a list of elements
        Document responseFile = Jsoup.parse(response);
        Elements Ptags = responseFile.select("p");

        //set all arrays based on size of list
        numStrings = Ptags.size();
        titles = new String[numStrings - 2];
        details = new String[numStrings - 2];
        searches = new ParsedData[numStrings - 2];

        //fill the data in the searches array
        for (int i = 2; i < numStrings; i++) {
            ParsedData temp = new ParsedData();
            String data = Ptags.get(i).text();
            String year = data.substring(8, 10);
            year = "20" + year;
            String JulianDay = data.substring(10, 13);

            JulianDay = year + JulianDay;
            String Date = JulianToDate(JulianDay);
            temp.setData(data);
            temp.setDate(Date);
            temp.setSecs(data.substring(17, 19));
            temp.setMins(data.substring(15, 17));
            temp.setHour(data.substring(13, 15));
            searches[i - 2] = temp;


        }

        //fill the display arrays based on the searches
        for (int i = 2; i < numStrings; i++) {
            details[i - 2] = searches[i - 2].getData();
            titles[i - 2] = searches[i - 2].getDate() + " Hours: " + searches[i - 2].getHour()
             + ":" + searches[i - 2].getMins();
        }


    }

    // Calculates JulianDate
    String JulianToDate(String julian) {
        String dateStr = julian;
        Calendar cal  = new GregorianCalendar();
        cal.set(Calendar.YEAR,Integer.parseInt(dateStr.substring(0,4)));
        cal.set(Calendar.DAY_OF_YEAR,Integer.parseInt(dateStr.substring(4)));
        Date parsedDate  = cal.getTime();

        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateToString = formatter.format(parsedDate);

        return dateToString;

    }

    // Explains itself.  Recreates MainActivity and passes in the selected string.
    void backToMainActivity(int position) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("Detail", details[position]);
        context.startActivity(intent);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView itemTitle;
        public TextView itemDetail;
        public int currentItem;

        public ViewHolder(View itemView) {
            super(itemView);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemDetail = (TextView) itemView.findViewById(R.id.item_detail);

            // Listens for a touch on the desired item.  It will proceed
            // to call backToMainActivity.
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    backToMainActivity(position);
                }
            });
        }
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.display_layout, viewGroup, false);
        MyAdapter.ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    // sets the text
    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int i) {
        viewHolder.itemTitle.setText(titles[i]);
        viewHolder.itemDetail.setText(details[i]);
    }

    // Number of boxes displayed
    @Override
    public int getItemCount() {
        return titles.length;
    }
}

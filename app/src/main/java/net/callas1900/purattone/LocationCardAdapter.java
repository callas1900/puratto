package net.callas1900.purattone;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.googlecode.flickrjandroid.places.Place;
import com.googlecode.flickrjandroid.places.PlacesList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.callas1900.purattone.flickr.GetLocationFlickrTask.PLACE_ID;
import static net.callas1900.purattone.flickr.GetLocationFlickrTask.WOE_ID;

/**
 *
 */
public class LocationCardAdapter extends RecyclerView.Adapter<LocationCardAdapter.CardViewHolder> {

    private List<Place> placeList;
    private int lastPosition = -1;

    public LocationCardAdapter(PlacesList places) {
        placeList = new ArrayList<>();
        Iterator<Place> it = places.iterator();
        while (it.hasNext()) {
            Place place = it.next();
            placeList.add(place);
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Place place = placeList.get(position);
        holder.vTitle.setText(place.getName());
        holder.vLatiLong.setText(place.getLatitude() + "," + place.getLongitude());
        holder.setPlaceId(place.getPlaceId());
        holder.setWoeId(place.getWoeId());

        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public void clearAll() {
        int size = placeList.size();
        if (placeList != null && size > 0) {
            for (int i = 0; i < size; i++) {
                placeList.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.push_up_in);
            animation.setDuration(600);
            animation.setInterpolator(viewToAnimate.getContext(), android.R.anim.accelerate_interpolator);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    /**
     *
     */
    public static class CardViewHolder extends RecyclerView.ViewHolder {

        protected TextView vTitle;
        protected TextView vLatiLong;
        private String placeId;
        private String woeId;

        public CardViewHolder(final View itemView) {
            super(itemView);
            vTitle = (TextView) itemView.findViewById(R.id.title);
            vLatiLong = (TextView) itemView.findViewById(R.id.txtName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), ViewerActivity.class);
                    intent.putExtra(WOE_ID, woeId);
                    intent.putExtra(PLACE_ID, placeId);
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        public void setWoeId(String woeId) {
            this.woeId = woeId;
        }

    }
}

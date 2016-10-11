package br.com.betfriend.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import br.com.betfriend.R;
import br.com.betfriend.model.Bet;
import br.com.betfriend.model.Match;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.BetResultEnum;
import br.com.betfriend.utils.CircleTransformation;
import br.com.betfriend.utils.ConvertHelper;
import br.com.betfriend.utils.TeamsDataEnum;

public class BetsExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private final Context context;

    private ArrayList<Bet> bets;

    private UserDataDTO userData;

    static class ViewHolderGroup {
        public TextView homeTeam;
        public TextView awayTeam;
        public TextView matchId;
        public TextView betStatus;
        public ImageView homeLogo;
        public ImageView awayLogo;
        public TextView amount;
        public ImageView personPhoto;
        public TextView personName;
        public TextView dateFinished;
    }

    static class ViewHolderChild {
        public TextView betId;
        public TextView friendOption;
        public RadioGroup radioGroup;
        public RadioButton homeRadioButton;
        public RadioButton drawRadioButton;
        public RadioButton awayRadioButton;
    }

    @Override
    public View getRealChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

        View rowView = convertView;

        // reuse views
        if (rowView == null) {

            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.bet_history_list_item, parent, false);

            ViewHolderChild viewHolderChild = new ViewHolderChild();
            viewHolderChild.betId = (TextView) rowView.findViewById(R.id.bet_id);
            viewHolderChild.friendOption = (TextView) rowView.findViewById(R.id.friend_option);
            viewHolderChild.radioGroup = (RadioGroup) rowView.findViewById(R.id.radio_group);
            viewHolderChild.homeRadioButton = (RadioButton) rowView.findViewById(R.id.radio_team_1);
            viewHolderChild.drawRadioButton = (RadioButton) rowView.findViewById(R.id.radio_draw);
            viewHolderChild.awayRadioButton = (RadioButton) rowView.findViewById(R.id.radio_team_2);

            rowView.setTag(viewHolderChild);
        }

        final ViewHolderChild holder = (ViewHolderChild) rowView.getTag();

        String homeTeam = bets.get(groupPosition).getMatch().getHomeTeam().trim();
        String awayTeam = bets.get(groupPosition).getMatch().getAwayTeam().trim();
        String betId = bets.get(groupPosition).get_id();

        String option = bets.get(groupPosition).getOption();
        String notChosenOption = bets.get(groupPosition).getNotChosenOption();

        holder.homeRadioButton.setText(TeamsDataEnum.get(homeTeam).label());
        holder.awayRadioButton.setText(TeamsDataEnum.get(awayTeam).label());
        holder.betId.setText(betId);

        Drawable imgSrc = getDrawableFromURL(bets.get(groupPosition).getSrcPerson().getPersonPhoto());
        Drawable imgDest = getDrawableFromURL(bets.get(groupPosition).getDestPerson().getPersonPhoto());

        holder.homeRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        holder.drawRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        holder.awayRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        // Set radio buttons
        if (option.equals("1")) {

            holder.homeRadioButton.setCompoundDrawablesWithIntrinsicBounds(imgSrc, null, null, null);

            if(notChosenOption.equals("X")) {
                holder.awayRadioButton.setCompoundDrawablesWithIntrinsicBounds(imgDest, null, null, null);
            } else {
                holder.drawRadioButton.setCompoundDrawablesWithIntrinsicBounds(imgDest, null, null, null);
            }

        } else if (option.equals("X")) {

            holder.drawRadioButton.setCompoundDrawablesWithIntrinsicBounds(imgSrc, null, null, null);

            if(notChosenOption.equals("1")) {
                holder.awayRadioButton.setCompoundDrawablesWithIntrinsicBounds(imgDest, null, null, null);
            } else {
                holder.homeRadioButton.setCompoundDrawablesWithIntrinsicBounds(imgDest, null, null, null);
            }

        } else {
            holder.awayRadioButton.setCompoundDrawablesWithIntrinsicBounds(imgSrc, null, null, null);

            if(notChosenOption.equals("1")) {
                holder.drawRadioButton.setCompoundDrawablesWithIntrinsicBounds(imgDest, null, null, null);
            } else {
                holder.homeRadioButton.setCompoundDrawablesWithIntrinsicBounds(imgDest, null, null, null);
            }
        }

        return rowView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 1;
    }

    public BetsExpandableListAdapter(Context context, ArrayList<Bet> bets, UserDataDTO userData) {
        this.context = context;
        this.bets = bets;
        this.userData = userData;
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return bets.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return this.bets.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.bet_history_list_group, null);

            // configure view holder
            ViewHolderGroup viewHolderGroup = new ViewHolderGroup();
            viewHolderGroup.homeTeam = (TextView) convertView.findViewById(R.id.home_team);
            viewHolderGroup.awayTeam = (TextView) convertView.findViewById(R.id.away_team);
            viewHolderGroup.matchId = (TextView) convertView.findViewById(R.id.match_id);
            viewHolderGroup.betStatus = (TextView) convertView.findViewById(R.id.bet_status);
            viewHolderGroup.homeLogo = (ImageView) convertView.findViewById(R.id.home_logo);
            viewHolderGroup.awayLogo = (ImageView) convertView.findViewById(R.id.away_logo);
            viewHolderGroup.amount = (TextView) convertView.findViewById(R.id.amount);
            viewHolderGroup.personName = (TextView) convertView.findViewById(R.id.person_name);
            viewHolderGroup.personPhoto = (ImageView) convertView.findViewById(R.id.person_photo);
            viewHolderGroup.dateFinished = (TextView) convertView.findViewById(R.id.date_finished);

            convertView.setTag(viewHolderGroup);
        }

        if (isExpanded) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.very_light_grey));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        // fill data
        ViewHolderGroup holder = (ViewHolderGroup) convertView.getTag();

        String homeTeam = bets.get(groupPosition).getMatch().getHomeTeam().trim();
        String awayTeam = bets.get(groupPosition).getMatch().getAwayTeam().trim();
        String matchId = bets.get(groupPosition).getMatch().getMatchId().toString();
        String personName = bets.get(groupPosition).getDestPerson().getPersonName();
        String personPhoto = bets.get(groupPosition).getDestPerson().getPersonPhoto();
        Integer result = bets.get(groupPosition).getResult();
        Integer amount = bets.get(groupPosition).getAmount();

        holder.homeTeam.setText(TeamsDataEnum.get(homeTeam).label());
        holder.awayTeam.setText(TeamsDataEnum.get(awayTeam).label());
        holder.matchId.setText(matchId);
        holder.personName.setText(personName);
        holder.amount.setText(amount.toString());
        holder.betStatus.setText(BetResultEnum.get(result).label());
        holder.betStatus.setBackground(context.getDrawable(BetResultEnum.get(result).color()));

        Date dateFinished = bets.get(groupPosition).getDateFinished();
        if(dateFinished != null) {
            holder.dateFinished.setText(ConvertHelper.dateToView(dateFinished));
            holder.dateFinished.setVisibility(View.VISIBLE);
        }

        Picasso.with(context)
                .load(TeamsDataEnum.get(homeTeam).logo())
                .fit()
                .into(holder.homeLogo);

        Picasso.with(context)
                .load(TeamsDataEnum.get(awayTeam).logo())
                .fit()
                .into(holder.awayLogo);

        Picasso.with(context)
                .load(personPhoto)
                .transform(new CircleTransformation())
                .into(holder.personPhoto);

        return convertView;
    }

    public static Drawable getDrawableFromURL(String url) {

        Bitmap x;

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();

            InputStream input = connection.getInputStream();
            x = BitmapFactory.decodeStream(input);

            return new BitmapDrawable((new CircleTransformation()).transform(x));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setUserData(UserDataDTO user) {
        userData = user;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

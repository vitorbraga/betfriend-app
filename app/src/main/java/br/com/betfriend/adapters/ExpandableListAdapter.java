package br.com.betfriend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.betfriend.R;
import br.com.betfriend.model.SoccerMatch;
import br.com.betfriend.utils.ConvertHelper;

public class ExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    static class ViewHolder {
        public TextView homeTeam;
        public TextView awayTeam;
        public TextView matchTime;
    }

    @Override
    public View getRealChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        View rowView = convertView;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.match_list_item, null);
        }

        return rowView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 1;
    }

    private final Context context;

    private ArrayList<SoccerMatch> matches;

    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, ArrayList<SoccerMatch> matches) {
        this.context = context;
        this.matches = matches;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return matches.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return this.matches.size();
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
            convertView = infalInflater.inflate(R.layout.match_list_group, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.homeTeam = (TextView) convertView.findViewById(R.id.home_team);
            viewHolder.awayTeam = (TextView) convertView.findViewById(R.id.away_team);
            viewHolder.matchTime = (TextView) convertView.findViewById(R.id.match_time);

            convertView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) convertView.getTag();

        String homeTeam = matches.get(groupPosition).getHomeTeam();
        String awayTeam = matches.get(groupPosition).getAwayTeam();
        Long tsTamp = matches.get(groupPosition).getTstamp();

        Date date = new Date(1000 * tsTamp);

        holder.homeTeam.setText(homeTeam);
        holder.awayTeam.setText(awayTeam);
        holder.matchTime.setText(ConvertHelper.dateToView(date));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

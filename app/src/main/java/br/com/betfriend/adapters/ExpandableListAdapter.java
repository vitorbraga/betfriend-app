package br.com.betfriend.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.betfriend.R;
import br.com.betfriend.StartBetActivity;
import br.com.betfriend.model.SoccerMatch;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.CircleTransformation;
import br.com.betfriend.utils.ConvertHelper;
import br.com.betfriend.utils.TeamsDataEnum;

public class ExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private final Context context;

    private ArrayList<SoccerMatch> matches;

    private UserDataDTO userData;

    private HashMap<String, List<String>> _listDataChild;

    static class ViewHolderGroup {
        public TextView homeTeam;
        public TextView awayTeam;
        public TextView matchTime;
        public TextView matchId;
        public ImageView homeLogo;
        public ImageView awayLogo;
    }

    static class ViewHolderChild {
        public TextView homeTeam;
        public TextView awayTeam;
        public Button sendButton;
        public TextView matchId;
        public SeekBar seekBar;
        public EditText betValue;
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

            ViewHolderChild viewHolderChild = new ViewHolderChild();
            viewHolderChild.homeTeam = (TextView) rowView.findViewById(R.id.radio_team_1);
            viewHolderChild.awayTeam = (TextView) rowView.findViewById(R.id.radio_team_2);
            viewHolderChild.sendButton = (Button) rowView.findViewById(R.id.bet_button);
            viewHolderChild.matchId = (TextView) rowView.findViewById(R.id.match_id);
            viewHolderChild.seekBar = (SeekBar) rowView.findViewById(R.id.seek_bar);
            viewHolderChild.betValue = (EditText) rowView.findViewById(R.id.bet_value);

            rowView.setTag(viewHolderChild);
        }

        final ViewHolderChild holder = (ViewHolderChild) rowView.getTag();

        String homeTeam = matches.get(groupPosition).getHomeTeam().trim();
        String awayTeam = matches.get(groupPosition).getAwayTeam().trim();
        String matchId = matches.get(groupPosition).getMatchId().toString();

        holder.homeTeam.setText(TeamsDataEnum.get(homeTeam).correctName());
        holder.awayTeam.setText(TeamsDataEnum.get(awayTeam).correctName());
        holder.matchId.setText(matchId);

        holder.seekBar.setMax(userData.getPoints());
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                holder.betValue.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        holder.betValue.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().equals("")) {
                    holder.seekBar.setProgress(0);
                } else {
                    holder.seekBar.setProgress(Integer.parseInt(s.toString()));
                }
            }

        });

        holder.sendButton.setOnClickListener(mInviteFriendClickListener);

        return rowView;
    }

    private static SoccerMatch getMatchById(Integer matchId, ArrayList<SoccerMatch> matches) {

        for(SoccerMatch sm : matches) {
            if(sm.getMatchId().equals(matchId)) {
                return sm;
            }
        }

        return null;
    }

    private View.OnClickListener mInviteFriendClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            LinearLayout viewParent = (LinearLayout) v.getParent();
            RadioGroup radioGroup = (RadioGroup) viewParent.findViewById(R.id.radio_group);
            int checked = radioGroup.getCheckedRadioButtonId();

            SeekBar seekBar = (SeekBar) viewParent.findViewById(R.id.seek_bar);
            int amount = seekBar.getProgress();

            // Validation amount
            if(amount == 0 || amount > userData.getPoints()) {
                Toast.makeText(context, "Valor não permitido para aposta.", Toast.LENGTH_SHORT).show();
                return;
            }

            String matchId = ((TextView) viewParent.findViewById(R.id.match_id)).getText().toString();
            String betOption = "";

            SoccerMatch match = getMatchById(Integer.parseInt(matchId), matches);

            // Validation result choice
            switch(checked) {
                case R.id.radio_team_1:
                    Log.d("Checked: ", "team1");
                    betOption = "1";
                    break;
                case R.id.radio_draw:
                    Log.d("Checked: ", "draw");
                    betOption = "X";
                    break;
                case R.id.radio_team_2:
                    Log.d("Checked: ", "team2");
                    betOption = "2";
                    break;
                default:
                    Log.d("Checked: ", "No option selected");
                    Toast.makeText(context, "Selecione um resultado", Toast.LENGTH_SHORT).show();
                    return;
            }

            Intent intent = new Intent(context, StartBetActivity.class);
            intent.putExtra("BET_OPTION_EXTRA", betOption);
            intent.putExtra("MATCH_ID_EXTRA", matchId);
            intent.putExtra("MATCH_EXTRA", match);
            intent.putExtra("AMOUNT", amount);
            intent.putExtra("USER_DATA_EXTRA", userData);
            context.startActivity(intent);
        }
    };

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 1;
    }

    public ExpandableListAdapter(Context context, ArrayList<SoccerMatch> matches, UserDataDTO userData) {
        this.context = context;
        this.matches = matches;
        this.userData = userData;
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
            ViewHolderGroup viewHolderGroup = new ViewHolderGroup();
            viewHolderGroup.homeTeam = (TextView) convertView.findViewById(R.id.home_team);
            viewHolderGroup.awayTeam = (TextView) convertView.findViewById(R.id.away_team);
            viewHolderGroup.matchTime = (TextView) convertView.findViewById(R.id.match_time);
            viewHolderGroup.matchId = (TextView) convertView.findViewById(R.id.match_id);
            viewHolderGroup.homeLogo = (ImageView) convertView.findViewById(R.id.home_logo);
            viewHolderGroup.awayLogo = (ImageView) convertView.findViewById(R.id.away_logo);

            convertView.setTag(viewHolderGroup);
        }

        // fill data
        ViewHolderGroup holder = (ViewHolderGroup) convertView.getTag();

        String homeTeam = matches.get(groupPosition).getHomeTeam().trim();
        String awayTeam = matches.get(groupPosition).getAwayTeam().trim();
        String matchId =  matches.get(groupPosition).getMatchId().toString();
        Long tsTamp = matches.get(groupPosition).getTstamp();

        Date date = new Date(1000 * tsTamp);

        holder.homeTeam.setText(TeamsDataEnum.get(homeTeam).label());
        holder.awayTeam.setText(TeamsDataEnum.get(awayTeam).label());
        holder.matchId.setText(matchId);
        holder.matchTime.setText(ConvertHelper.dateToView(date));

        Picasso.with(context)
                .load(TeamsDataEnum.get(homeTeam).logo())
                .fit()
                .into(holder.homeLogo);

        Picasso.with(context)
                .load(TeamsDataEnum.get(awayTeam).logo())
                .fit()
                .into(holder.awayLogo);

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

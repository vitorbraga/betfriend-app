package br.com.betfriend.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import br.com.betfriend.R;
import br.com.betfriend.SearchFriendActivity;
import br.com.betfriend.databinding.MatchListItemBinding;
import br.com.betfriend.model.Match;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.ConvertHelper;
import br.com.betfriend.utils.TeamsDataEnum;

public class MatchesExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private final Context context;

    private ArrayList<Match> matches;

    private UserDataDTO userData;

    private MatchListItemBinding mBinding;

    private static class ViewHolderGroup {
        TextView homeTeam;
        TextView awayTeam;
        TextView matchTime;
        TextView matchId;
        ImageView homeLogo;
        ImageView awayLogo;
    }

    private static class ViewHolderChild {
        Button sendButton;
        TextView matchId;
        SeekBar seekBar;
        EditText betValue;
        RadioButton homeButton;
        RadioButton drawButton;
        RadioButton awayButton;
    }

    @Override
    public View getRealChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ViewHolderChild holder;

        // reuse views
        if (rowView == null) {

            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            mBinding = DataBindingUtil.inflate(inflater, R.layout.match_list_item, parent, false);
            rowView = mBinding.getRoot();
            mBinding.setUser(userData);

            holder = new ViewHolderChild();
            holder.sendButton = (Button) rowView.findViewById(R.id.bet_button);
            holder.matchId = (TextView) rowView.findViewById(R.id.match_id);
            holder.seekBar = (SeekBar) rowView.findViewById(R.id.seek_bar);
            holder.betValue = (EditText) rowView.findViewById(R.id.bet_value);
            holder.homeButton = (RadioButton) rowView.findViewById(R.id.radio_team_1);
            holder.drawButton = (RadioButton) rowView.findViewById(R.id.radio_draw);
            holder.awayButton = (RadioButton) rowView.findViewById(R.id.radio_team_2);

            rowView.setTag(holder);

        } else {
            holder = (ViewHolderChild) rowView.getTag();
        }

        String homeTeam = matches.get(groupPosition).getHomeTeam().trim();

        String awayTeam = matches.get(groupPosition).getAwayTeam().trim();
        String matchId = matches.get(groupPosition).getMatchId().toString();

        TeamsDataEnum homeTeamEnum = TeamsDataEnum.get(homeTeam);
        TeamsDataEnum awayTeamEnum = TeamsDataEnum.get(awayTeam);

        final EditText betValue = holder.betValue;
        final SeekBar seekBar = holder.seekBar;

        holder.homeButton.setText(homeTeamEnum.correctName());
        holder.awayButton.setText(awayTeamEnum.correctName());
        holder.matchId.setText(matchId);

        holder.homeButton.setChecked(false);
        holder.drawButton.setChecked(false);
        holder.awayButton.setChecked(false);

        holder.seekBar.setMax(userData.getPoints());
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                betValue.setText(context.getString(R.string.progress, progress));
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

                if (s.toString().equals("")) {
                    seekBar.setProgress(0);
                } else {
                    seekBar.setProgress(Integer.parseInt(s.toString()));
                }
            }

        });

        holder.sendButton.setOnClickListener(mInviteFriendClickListener);

        return rowView;
    }

    private static Match getMatchById(Integer matchId, ArrayList<Match> matches) {

        for (Match sm : matches) {
            if (sm.getMatchId().equals(matchId)) {
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
            if (amount == 0 || amount > userData.getPoints()) {
                Toast.makeText(context, context.getString(R.string.value_not_allowed), Toast.LENGTH_SHORT).show();
                return;
            }

            String matchId = ((TextView) viewParent.findViewById(R.id.match_id)).getText().toString();
            String betOption = "";

            Match match = getMatchById(Integer.parseInt(matchId), matches);

            // Validation result choice
            switch (checked) {
                case R.id.radio_team_1:
                    betOption = "1";
                    break;
                case R.id.radio_draw:
                    betOption = "X";
                    break;
                case R.id.radio_team_2:
                    betOption = "2";
                    break;
                default:
                    Toast.makeText(context, context.getString(R.string.choose_some_result), Toast.LENGTH_SHORT).show();
                    return;
            }

            Intent intent = new Intent(context, SearchFriendActivity.class);
            intent.putExtra("BET_OPTION_EXTRA", betOption);
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

    public MatchesExpandableListAdapter(Context context, ArrayList<Match> matches, UserDataDTO userData) {
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

        ViewHolderGroup holder;

        if (convertView == null) {

            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.match_list_group, null);

            // configure view holder
            holder = new ViewHolderGroup();
            holder.homeTeam = (TextView) convertView.findViewById(R.id.home_team);
            holder.awayTeam = (TextView) convertView.findViewById(R.id.away_team);
            holder.matchTime = (TextView) convertView.findViewById(R.id.match_time);
            holder.matchId = (TextView) convertView.findViewById(R.id.match_id);
            holder.homeLogo = (ImageView) convertView.findViewById(R.id.home_logo);
            holder.awayLogo = (ImageView) convertView.findViewById(R.id.away_logo);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolderGroup) convertView.getTag();
        }

        if (isExpanded) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.very_light_grey));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        // Fill data
        String homeTeam = matches.get(groupPosition).getHomeTeam().trim();
        String awayTeam = matches.get(groupPosition).getAwayTeam().trim();
        String matchId = matches.get(groupPosition).getMatchId().toString();
        Date date = matches.get(groupPosition).getTstamp();

        TeamsDataEnum homeTeamEnum = TeamsDataEnum.get(homeTeam);
        TeamsDataEnum awayTeamEnum = TeamsDataEnum.get(awayTeam);

        holder.homeTeam.setText(homeTeamEnum.label());
        holder.awayTeam.setText(awayTeamEnum.label());
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

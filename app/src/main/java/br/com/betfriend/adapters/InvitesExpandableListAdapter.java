package br.com.betfriend.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
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

import br.com.betfriend.BetAcceptedActivity;
import br.com.betfriend.BetCompleteActivity;
import br.com.betfriend.R;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.databinding.InviteBetListItemBinding;
import br.com.betfriend.model.Bet;
import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.model.Match;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.CircleTransformation;
import br.com.betfriend.utils.TeamsDataEnum;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InvitesExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private final Context context;

    private ArrayList<Bet> bets;

    private UserDataDTO userData;
    private InviteBetListItemBinding mBinding;

    static class ViewHolderGroup {
        public TextView homeTeam;
        public TextView awayTeam;
        public TextView matchId;
        public ImageView homeLogo;
        public ImageView awayLogo;
        public ImageView personPhoto;
        public TextView personName;
    }

    static class ViewHolderChild {
        public TextView homeTeam;
        public TextView awayTeam;
        public Button acceptButton;
        public Button refuseButton;
        public TextView betId;
        public TextView matchId;
        public TextView friendOption;
        public TextView friendId;
        public SeekBar seekBar;
        public EditText betValue;
        public RadioGroup radioGroup;
        public RadioButton homeButton;
        public RadioButton drawButton;
        public RadioButton awayButton;
    }

    @Override
    public View getRealChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

        View rowView = convertView;

        // reuse views
        if (rowView == null) {

            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            rowView = inflater.inflate(R.layout.match_list_item, null);

            mBinding = DataBindingUtil.inflate(inflater, R.layout.invite_bet_list_item, parent, false);
            rowView = mBinding.getRoot();
            mBinding.setUserInvite(userData);

            ViewHolderChild viewHolderChild = new ViewHolderChild();
            viewHolderChild.homeTeam = (TextView) rowView.findViewById(R.id.radio_team_1);
            viewHolderChild.awayTeam = (TextView) rowView.findViewById(R.id.radio_team_2);
            viewHolderChild.acceptButton = (Button) rowView.findViewById(R.id.accept_button);
            viewHolderChild.refuseButton = (Button) rowView.findViewById(R.id.refuse_button);
            viewHolderChild.betId = (TextView) rowView.findViewById(R.id.bet_id);
            viewHolderChild.matchId = (TextView) rowView.findViewById(R.id.match_id);
            viewHolderChild.friendOption = (TextView) rowView.findViewById(R.id.friend_option);
            viewHolderChild.friendId = (TextView) rowView.findViewById(R.id.friend_id);
            viewHolderChild.seekBar = (SeekBar) rowView.findViewById(R.id.seek_bar);
            viewHolderChild.betValue = (EditText) rowView.findViewById(R.id.bet_value);
            viewHolderChild.radioGroup = (RadioGroup) rowView.findViewById(R.id.radio_group);
            viewHolderChild.homeButton = (RadioButton) rowView.findViewById(R.id.radio_team_1);
            viewHolderChild.drawButton = (RadioButton) rowView.findViewById(R.id.radio_draw);
            viewHolderChild.awayButton = (RadioButton) rowView.findViewById(R.id.radio_team_2);

            rowView.setTag(viewHolderChild);
        }

        final ViewHolderChild holder = (ViewHolderChild) rowView.getTag();

        String homeTeam = bets.get(groupPosition).getMatch().getHomeTeam().trim();
        String awayTeam = bets.get(groupPosition).getMatch().getAwayTeam().trim();
        String matchId = bets.get(groupPosition).getMatch().getMatchId().toString();
        String betId = bets.get(groupPosition).get_id();
        String friendId = bets.get(groupPosition).getSrcPerson().getPersonId();

        String option = bets.get(groupPosition).getOption();

        holder.homeTeam.setText(TeamsDataEnum.get(homeTeam).correctName());
        holder.awayTeam.setText(TeamsDataEnum.get(awayTeam).correctName());
        holder.matchId.setText(matchId);
        holder.betId.setText(betId);
        holder.friendId.setText(friendId);

        // Set seekbar
        holder.seekBar.setMax(userData.getPoints());
        holder.seekBar.setProgress(bets.get(groupPosition).getAmount());
        holder.seekBar.setEnabled(false);

        // Set ragio buttons
        if (option.equals("1")) {
            holder.homeButton.setEnabled(false);
            holder.friendOption.setText(holder.homeButton.getId() + "");
        } else if (option.equals("X")) {
            holder.drawButton.setEnabled(false);
            holder.friendOption.setText(holder.drawButton.getId() + "");
        } else {
            holder.awayButton.setEnabled(false);
            holder.friendOption.setText(holder.awayButton.getId() + "");
        }

        // Set edittext
        holder.betValue.setText(bets.get(groupPosition).getAmount().toString());

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String option = "", matchId = "", betId = "", friendId = "";

                LinearLayout viewParent = (LinearLayout) v.getParent().getParent();

                matchId = ((TextView) viewParent.findViewById(R.id.match_id)).getText().toString();
                betId = ((TextView) viewParent.findViewById(R.id.bet_id)).getText().toString();
                friendId = ((TextView) viewParent.findViewById(R.id.friend_id)).getText().toString();

                RadioGroup radioGroup = (RadioGroup) viewParent.findViewById(R.id.radio_group);
                int checked = radioGroup.getCheckedRadioButtonId();
                int friendOption = Integer.parseInt(((TextView) viewParent
                        .findViewById(R.id.friend_option)).getText().toString());

                // Validating radio buttons
                if (checked == -1) {
                    Toast.makeText(context, "Escolha um resultado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checked == friendOption) {
                    Toast.makeText(context, "Resultado já escolhido. Escolha outro.", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (checked) {
                    case R.id.radio_team_1:
                        option = "1";
                        break;
                    case R.id.radio_draw:
                        option = "X";
                        break;
                    case R.id.radio_team_2:
                        option = "2";
                        break;
                }

                SeekBar seekBar = (SeekBar) viewParent.findViewById(R.id.seek_bar);
                int amount = seekBar.getProgress();
                if (amount == 0 || amount > userData.getPoints()) {
                    Toast.makeText(context, "Valor não permitido para aposta.", Toast.LENGTH_SHORT).show();
                    return;
                }

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(context.getString(R.string.server_uri)).build();

                ServerApi api = restAdapter.create(ServerApi.class);

                api.acceptBet(betId, userData.getPersonId(), friendId, matchId, option, amount, new Callback<JsonResponse>() {

                    @Override
                    public void success(JsonResponse json, Response response) {
                        Toast.makeText(context, "aceito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, BetAcceptedActivity.class);
                        context.startActivity(intent);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(context, "falha no aceito.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        holder.refuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "refuse", Toast.LENGTH_SHORT).show();
            }
        });

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

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 1;
    }

    public InvitesExpandableListAdapter(Context context, ArrayList<Bet> bets, UserDataDTO userData) {
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
            convertView = infalInflater.inflate(R.layout.invite_bet_list_group, null);

            // configure view holder
            ViewHolderGroup viewHolderGroup = new ViewHolderGroup();
            viewHolderGroup.homeTeam = (TextView) convertView.findViewById(R.id.home_team);
            viewHolderGroup.awayTeam = (TextView) convertView.findViewById(R.id.away_team);
            viewHolderGroup.matchId = (TextView) convertView.findViewById(R.id.match_id);
            viewHolderGroup.homeLogo = (ImageView) convertView.findViewById(R.id.home_logo);
            viewHolderGroup.awayLogo = (ImageView) convertView.findViewById(R.id.away_logo);
            viewHolderGroup.personName = (TextView) convertView.findViewById(R.id.person_name);
            viewHolderGroup.personPhoto = (ImageView) convertView.findViewById(R.id.person_photo);

            convertView.setTag(viewHolderGroup);
        }

        // fill data
        ViewHolderGroup holder = (ViewHolderGroup) convertView.getTag();

        String homeTeam = bets.get(groupPosition).getMatch().getHomeTeam().trim();
        String awayTeam = bets.get(groupPosition).getMatch().getAwayTeam().trim();
        String matchId = bets.get(groupPosition).getMatch().getMatchId().toString();
        String personName = bets.get(groupPosition).getSrcPerson().getPersonName();
        String personPhoto = bets.get(groupPosition).getSrcPerson().getPersonPhoto();

        holder.homeTeam.setText(TeamsDataEnum.get(homeTeam).correctName());
        holder.awayTeam.setText(TeamsDataEnum.get(awayTeam).correctName());
        holder.matchId.setText(matchId);
        holder.personName.setText(personName);

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

    public void setUserData(UserDataDTO user) {
        userData = user;
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

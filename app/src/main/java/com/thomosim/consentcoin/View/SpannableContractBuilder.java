package com.thomosim.consentcoin.View;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.thomosim.consentcoin.Persistence.ModelClass.PermissionRequest;
import com.thomosim.consentcoin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SpannableContractBuilder {

    Context context;

    public SpannableContractBuilder(Context context) {
        this.context = context;
    }

    public SpannableStringBuilder displayPermissionRquest(PermissionRequest pr){
        ArrayList<Object> contractElements = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        contractElements.add(context.getString(R.string.contract_text_part_one));
        contractElements.add(new SpannableStringBuilder(pr.getMemberName()));
        contractElements.add(context.getString(R.string.contract_text_part_two));
        contractElements.add(new SpannableStringBuilder(pr.getOrganizationName()));
        contractElements.add(context.getString(R.string.contract_text_part_three));
        contractElements.add(new SpannableStringBuilder(pr.getPersonsIncluded().getScope()));
        contractElements.add(context.getString(R.string.contract_text_part_four));
        contractElements.add(new SpannableStringBuilder(pr.getPermissionType().getType()));
        contractElements.add(context.getString(R.string.contract_text_part_five));
        contractElements.add(new SpannableStringBuilder(simpleDateFormat.format(pr.getPermissionStartDate())));
        contractElements.add(context.getString(R.string.contract_text_part_six));
        contractElements.add(new SpannableStringBuilder(simpleDateFormat.format(pr.getPermissionEndDate())));


        return createContractText(contractElements, R.color.colorBitterLemon, 1.2f, R.font.noto_sans);
    }

    public SpannableStringBuilder displayInviteRequest(String orgName){
        ArrayList<Object> inviteElements = new ArrayList<>();

        inviteElements.add(new SpannableStringBuilder(orgName));
        inviteElements.add(context.getString(R.string.invite_message));

        return createContractText(inviteElements, R.color.colorBitterLemon, 1.2f, R.font.noto_sans);

    }

    public SpannableStringBuilder displayConsentcoin(String id, String mem, String org, String type){
        ArrayList<Object> coinElements = new ArrayList<>();

        coinElements.add(context.getString(R.string.consentcoin_value_id));
        coinElements.add(new SpannableStringBuilder(id));
        coinElements.add(context.getString(R.string.consentcoin_value_mem));
        coinElements.add(new SpannableStringBuilder(mem));
        coinElements.add(context.getString(R.string.consentcoin_value_org));
        coinElements.add(new SpannableStringBuilder(org));
        coinElements.add(context.getString(R.string.consentcoin_value_contract_type));
        coinElements.add(new SpannableStringBuilder(type));

        return createContractText(coinElements, R.color.colorBitterLemon, 1f, R.font.noto_sans);

    }

    //This method creates SpannableStringBuilders which have different colors, these will be added and appear as text
    public SpannableStringBuilder createContractText(ArrayList<Object> contractElements, int color, float fontSize, int font){
        SpannableStringBuilder completeContract = new SpannableStringBuilder();
        Typeface typeface = ResourcesCompat.getFont(context, font);

        for (Object o: contractElements) {
            SpannableStringBuilder element = null;
            if(Build.VERSION.SDK_INT > 22) {
                if (o instanceof SpannableStringBuilder) {
                    element = (SpannableStringBuilder) o;
                    element = setColorOfElement(element, color);

                } else if (o instanceof String) {
                    element = new SpannableStringBuilder((String) o);
                    element = setColorOfElement(element, R.color.colorRichBlack);
                }
            }
            element.setSpan(new CustomTypefaceSpan("", typeface), 0 , element.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            element.setSpan(new RelativeSizeSpan(fontSize), 0, element.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
            completeContract.append(element);
        }

        return completeContract;
    }

    //Simple method to change color of elements of the contract
    //The method was created to avoid phones with API less than 23 to see their invites
    @RequiresApi(api = Build.VERSION_CODES.M)
    public SpannableStringBuilder setColorOfElement(SpannableStringBuilder e, int color){
        e.setSpan(new ForegroundColorSpan(context.getColor(color)), 0, e.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return e;
    }
}

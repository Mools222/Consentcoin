package com.thomosim.consentcoin.View;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.thomosim.consentcoin.Persistence.ModelClass.PermissionRequest;
import com.thomosim.consentcoin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SpannableContractBuilder {
    private Context context;

    public SpannableContractBuilder(Context context) {
        this.context = context;
    }

    /**
     * Returns a SpannableStringBuilder containing a String representation of the PermissionRequest given as a parameter.
     * The customizable parts of the contract such as name, date and so on, will be shown with Bitter Lemon color.
     * This method is called from the ProcessRequestActivity class, when a user selects a pending PermissionRequest.
     *
     * @param pr A PermissionRequest Object
     * @return A SpannableStringBuilder Object
     */
    public SpannableStringBuilder displayPermissionRequest(PermissionRequest pr){
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

        return createContractText(contractElements, R.color.colorBitterLemon, R.font.noto_sans);
    }

    /**
     * Returns a SpannableStringBuilder Object with the String parameter shown in the color Bitter Lemon
     * This method is called from the ProcessInviteActivity class when the user selects a pending InviteRequest
     *
     * @param orgName A String representation of the name of an organisation
     * @return A SpannableStringBuilder Object
     */
    public SpannableStringBuilder displayInviteRequest(String orgName){
        ArrayList<Object> inviteElements = new ArrayList<>();

        inviteElements.add(new SpannableStringBuilder(orgName));
        inviteElements.add(context.getString(R.string.invite_message));

        return createContractText(inviteElements, R.color.colorBitterLemon, R.font.noto_sans);
    }

    /**
     * Returns a SpannableStringBuilder containing a String representation of the Strings given as parameters.
     * The parameters will be shown with Bitter Lemon color.
     * This method is called from the MyConsentcoinActivity class, when a user selects a Consentcoin.
     *
     * @param id A String with the ID of a Consentcoin
     * @param mem A string with the name of af Member
     * @param org A String with the name of an organization
     * @param type A String with the type of contract
     * @return A SpannableStringBuilder Object
     */
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

        return createContractText(coinElements, R.color.colorBitterLemon, R.font.noto_sans);
    }

    /**
     * Returns a single SpannableStringBuilder with all the Strings and SpannableStringBuilderObjects from
     * the contractElements ArrayList. It iterates through the ArrayList using the instanceof operator
     * to check whether an Object from the ArrayList is String or a spannableStringBuilder.
     * If it is a String it wil set its color to Rich Black, and if it is a SpannableStringBuilder it
     * will set its color to the color specified in the parameters.
     * After the color is changed all Objects is set to the specified font from the parameters, and
     * then added to new SpannableStringBuilder, which is returned when the foreach loop is done
     *
     * NOTE: The color will only change if the device running the app is running API 23 or higher
     *
     * @param contractElements An ArrayList of Objects, either Strings or SpannableStringBuilders
     * @param color The int representation of a color from the colors.xml file
     * @param font The int representation of a font from the res/font folder
     * @return A SpannableStringBuilder Object
     */
    public SpannableStringBuilder createContractText(ArrayList<Object> contractElements, int color, int font){
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
            completeContract.append(element);
        }

        return completeContract;
    }

    /**
     * Returns a SpannableStringBuilder in the color specified from the parameter.
     *
     * NOTE: This method will only be used if the device running the app is running API 23 or higher
     *
     * @param e A SpannableStringBuilder Object
     * @param color The int representation of a color from colors.xml
     * @return A SpannableStringBuilder
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public SpannableStringBuilder setColorOfElement(SpannableStringBuilder e, int color){
        e.setSpan(new ForegroundColorSpan(context.getColor(color)), 0, e.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return e;
    }
}

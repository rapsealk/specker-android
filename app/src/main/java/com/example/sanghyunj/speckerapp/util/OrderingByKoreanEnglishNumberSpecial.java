package com.example.sanghyunj.speckerapp.util;

import com.example.sanghyunj.speckerapp.model.FriendList.FriendListItem;

import java.util.Comparator;

import static com.example.sanghyunj.speckerapp.util.CharUtil.isEnglish;
import static com.example.sanghyunj.speckerapp.util.CharUtil.isKorean;
import static com.example.sanghyunj.speckerapp.util.CharUtil.isNumber;
import static com.example.sanghyunj.speckerapp.util.CharUtil.isSpecial;

/**
 * Created by sanghyunj on 09/06/2017.
 */

public class OrderingByKoreanEnglishNumberSpecial {
    private static final int REVERSE = -1;
    private static final int LEFT_FIRST = -1;
    private static final int RIGHT_FIRST = 1;

    public static Comparator<FriendListItem> getComparator(){
        return new Comparator<FriendListItem>() {
            @Override
            public int compare(FriendListItem o1, FriendListItem o2) {
                return OrderingByKoreanEnglishNumberSpecial.compare(o1.getName(), o2.getName());
            }
        };
    }

    public static int compare(String left, String right){
        left = left.toUpperCase().replaceAll(" ","");
        right = right.toUpperCase().replaceAll(" ","");

        int leftLen = left.length();
        int rightLen = right.length();
        int minLen = Math.min(leftLen, rightLen);

        for(int i=0; i<minLen; ++i){
            char leftChar = left.charAt(i);
            char rightChar = right.charAt(i);

            if(leftChar != rightChar){
                if(isKoreanAndEnglish(leftChar, rightChar)||
                        isKoreanAndNumber(leftChar, rightChar)||
                        isEnglishAndNumber(leftChar, rightChar)||
                        isKoreanAndSpecial(leftChar, rightChar)){
                    return (leftChar - rightChar)*REVERSE;
                }
            } else if(isEnglishAndSpecial(leftChar, rightChar)||
                    isNumberAndSpecial(leftChar, rightChar)){
                if(isEnglish(leftChar)||isNumber(leftChar)) {
                    return LEFT_FIRST;
                } else {
                    return RIGHT_FIRST;
                }
            } else {
                return leftChar - rightChar;
            }
        }
        return leftLen - rightLen;
    }


    private static boolean isKoreanAndEnglish(char ch1, char ch2){
        return (isEnglish(ch1)&&isKorean(ch2))||
                (isKorean(ch1)&&isEnglish(ch2));
    }

    private static boolean isKoreanAndNumber(char ch1, char ch2){
        return (isNumber(ch1) && isKorean(ch2))||
                (isKorean(ch1)&& isNumber(ch2));
    }

    private static boolean isEnglishAndNumber(char ch1, char ch2){
        return (isNumber(ch1)&&isEnglish(ch2))||
                (isEnglish(ch1)&&isNumber(ch2));
    }

    private static boolean isKoreanAndSpecial(char ch1, char ch2){
        return (isKorean(ch1)&&isSpecial(ch2))||
                (isSpecial(ch1)&&isKorean(ch2));
    }

    private static boolean isEnglishAndSpecial(char ch1, char ch2){
        return (isEnglish(ch1)&&isSpecial(ch2))||
                (isSpecial(ch1)&&isEnglish(ch2));
    }

    private static boolean isNumberAndSpecial(char ch1, char ch2){
        return (isNumber(ch1)&&isSpecial(ch2))||
                (isSpecial(ch1)&&isNumber(ch2));
    }
}

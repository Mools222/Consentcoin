package com.thomosim.consentcoin.Persistence.ModelClass.Exception;

public class ProfanityException extends Exception {

    public ProfanityException( ){
        super();
        }

    public String toString(){
        return "Profanity was found in the word";
    }
}

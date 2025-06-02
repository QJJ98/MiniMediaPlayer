package com.quinton.media;

import com.quinton.media.io.decoder.wav.WavFileDecoder;

/**
 *
 *
 */
public class MiniMediaPlayerApp {


    /**
     *
     * @param args
     */
    public static void main( String[] args ) {
        System.out.println("Printing contents of a WAVE file.");
        WavFileDecoder wavDecoder = new WavFileDecoder();
        wavDecoder.parseWavFileContents();
        wavDecoder.printWavFileContents();
    } // main

} // MiniMediaPlayerApp
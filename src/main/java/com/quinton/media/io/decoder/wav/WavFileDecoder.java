package com.quinton.media.io.decoder.wav;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;

/**
 *
 */
public class WavFileDecoder {

    private byte[] riffID, filetypeID, chunkID, dataID;
    private int fileSize, chunkSize, dataChunkSize;
    private int sampleRate, byteRate;
    private short audioFormat, numChannels, blockAlign, bitsPerSample;
    private short[] samples;
    private final String fileLoc = "src/main/resources/audio/wav/Backstreet_Brawler_140bpm_120s.wav"; // Test

    /**
     * Prints the contents of the WAVE file to console.
     */
    public void parseWavFileContents() {
        File wavFile = new File(fileLoc);
        if (!isWavFile(wavFile)) {
            throw new IllegalStateException("The file type is not correct. It should be a .wav file");
        }

        try (DataInputStream is = new DataInputStream(new FileInputStream(wavFile))) {
            // RIFF Header (first 4 bytes)
            riffID = new byte[4];
            is.readFully(riffID);
            fileSize = Integer.reverseBytes(is.readInt());
            filetypeID = new byte[4];
            is.readFully(filetypeID);
            // Format Chunk (second 4 bytes)
            chunkID = new byte[4];
            is.readFully(chunkID);
            chunkSize = Integer.reverseBytes(is.readInt());
            audioFormat = Short.reverseBytes(is.readShort());
            numChannels = Short.reverseBytes(is.readShort());
            sampleRate = Integer.reverseBytes(is.readInt());
            byteRate = Integer.reverseBytes(is.readInt());
            blockAlign = Short.reverseBytes(is.readShort());
            bitsPerSample = Short.reverseBytes(is.readShort());

            // Data Chunk (third 4 bytes)
            dataID = new byte[4];
            is.readFully(dataID);
            dataChunkSize = Integer.reverseBytes(is.readInt());

            int bytesPerSample = 2; // 2 bytes for 16-bit PCM
            int chunkSize = 4096; // 4 KB chunk size

            byte[] buffer = new byte[chunkSize];

            int rem = dataChunkSize; // Remaining bytes to read from data chunk
            samples = new short[dataChunkSize / (bitsPerSample / 8)];
            int i = 0;
            while (rem > 0) {
                int bytesToRead = Math.min(rem, buffer.length); // Bytes to be read
                int bytesRead = is.read(buffer, 0, bytesToRead); // Bytes already read

                // If the return value of is.read is -1 (at end of file), then break loop
                if (bytesRead == -1) {
                    break;
                }

                // Wrap buffer in ByteBuffer and set to little-endian
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead);
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

                // Process samples (every 2 bytes is equal to 1 sample)
                while (byteBuffer.remaining() >= bytesPerSample) {
                    samples[i] = byteBuffer.getShort();
                    i++;
                }

                rem -= bytesRead;

            }

        } catch (FileNotFoundException fnf) {
            System.err.println("The file: " + wavFile.getName() + " was not found.");
        } catch (IOException ioe) {
            System.out.println("An IO Exception occurred: " + ioe);
        }
    } // printWavFileContents

    /**
     *
     */
    public void printWavFileContents() {
        System.out.println("RIFF HEADER");
        System.out.println("Identifier: " + new String(riffID));
        System.out.println("File Size: " + fileSize + " bytes (" + ((fileSize / 1024) / 1024) + " MB)");
        System.out.println("File Type: " + new String(filetypeID));
        System.out.println("CHUNK HEADER");
        System.out.println("Chunk ID: " + new String(chunkID));
        System.out.println("Chunk Size: " + chunkSize);
        System.out.println("Audio Format: " + audioFormat);
        System.out.print("Channels: " + numChannels);
        if (numChannels == 1)
            System.out.print(" (Mono)\n");
        else if (numChannels == 2)
            System.out.print(" (Stereo)\n");
        System.out.println("Sample Rate: " + sampleRate + " Hz");
        System.out.println("Byte Rate: " + byteRate);
        System.out.println("Block Align: " + blockAlign);
        System.out.println("Bits per Sample: " + bitsPerSample + " bits");
        System.out.println("DATA CHUNK");
        System.out.println("Chunk ID: " + new String(dataID));
        System.out.println("Data Size: " + dataChunkSize + " bytes (" + ((dataChunkSize / 1024) / 1024) + " MB)");
        System.out.println("Data Sample:");
        for (short sample : samples) {
            System.out.print(sample + " | ");
        }
    } // printWavFIleContents


    /**
     * Returns {@code true} if the provided file is a WAVE file. This is determined by the presence
     * of the .wav file extension. Returns {@code false} if the file is on any other type.
     * @param file the file to be checked
     * @return {@code true} if the provided file is a WAVE file (with .wav file extension)
     */
    private boolean isWavFile(File file) {
        String fileType = "";
        String fileName = file.getName();
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i > 1) {
            extension = fileName.substring(i + 1);
        }
        try {
            fileType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            System.err.println("Cannot determine the file type of " + file.getName()
                    + " due to exception: " + e);
        }
        System.out.println("File Type: " + fileType);
        return extension.equals("wav");
    } // isWAVFile


} // WavFileDecoder
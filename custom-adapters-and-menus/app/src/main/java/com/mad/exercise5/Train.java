/*
 * Copyright 2018 Jake Holmes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mad.exercise5;

/**
 * Abstraction of the train item that will populate the data for the recycler view in main activity.
 */
public class Train {

    private String mPlatform;
    private int mArrivalTime;
    private String mStatus;
    private String mDestination;
    private String mDestinationTime;

    /**
     * Constructor to set all class instance variables.
     * @param platform The station and platform that the train will arrive at.
     * @param arrivalTime The time remaining in minutes until the train will arrive.
     * @param status The status of the train.
     * @param destination The destination station of the train.
     * @param destinationTime The time that the train will arrive at the destination.
     */
    public Train(String platform, int arrivalTime, String status, String destination, String destinationTime) {
        mPlatform = platform;
        mArrivalTime = arrivalTime;
        mStatus = status;
        mDestination = destination;
        mDestinationTime = destinationTime;
    }

    public String getPlatform() {
        return mPlatform;
    }

    public void setPlatform(String platform) {
        mPlatform = platform;
    }

    public int getArrivalTime() {
        return mArrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        mArrivalTime = arrivalTime;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String destination) {
        mDestination = destination;
    }

    public String getDestinationTime() {
        return mDestinationTime;
    }

    public void setDestinationTime(String destinationTime) {
        mDestinationTime = destinationTime;
    }
}

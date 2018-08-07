# DefectDetect
This is an android app that identifies potholes on a road and marks their location on a map as well as providing a detailed summary.

## Getting Started
To download and run the app you need to get [android studio](https://developer.android.com/studio/). Then clone this repo to get the code and simply follow the instructions provided [here](https://developer.android.com/training/basics/firstapp/running-app). This will allow you to create either a virtual device or use your own physical device.
You must also download the OpenCV 3.1 sdk from their website, and install OpenCV Manager using command:
'adb install <path-to-opencv3.1-download>/apk/OpenCV_3.1.0_Manager_3.10_<your-specific-architecture>.apk'

## App Features
This app uses a deep neural network called YOLO that is implemented on the Darknet framework. We trained the network on 1,800+ images in order to get accurate detections. This application has three different tabs: a map tab, a camera tab, and a list tab. The app automatically saves your data every time it is closed (but not if it crashes).
### Map Tab
This is where the application starts off when first opening it. It uses a google map to determine the user's location and show it on the map. Once a pothole is found, it will be displayed as a marker on the map. Clicking one of the markers will bring up more detailed information about the pothole in a text field at the bottom of the screen.
![alt text](https://github.com/pinsondg/DefectDetect/blob/master/screenshots/Screenshot_1533579507.png)
### Camera Tab
The camera tab is where the magic of the app really happens. This brings up the phone's back camera and uses the neural network to draw a box around any pothole it finds. Note that the first time you open this tab, you will be asked to confirm if you want the current camera view. On some devices, the camera view is sideways. If this is your case, just click the rotate button in the upper left-hand corner to rotate the camera view. Once the phone finds a detection on the camera, you will be able to see the pothole appear on the map and in the list.

![alt text](https://github.com/pinsondg/DefectDetect/blob/master/screenshots/Screenshot_1533580816.png)
### List Tab
The list tab displays all the potholes your device has found in an ordered list. To delete an item from the list simply swipe left on an item and press the delete button that appears. To search for a specific pothole, just start typing in the search bar and the list will be modified. Taping on a list item will bring the user back to the map and to the marker of the pothole they selected. Taping the green 'X' button in the bottom righthand corner will prompt the user if they want to clear the list.
![alt text](https://github.com/pinsondg/DefectDetect/blob/master/screenshots/Screenshot_1533579541.png)

## Modifying Code
If you would like to modify the code, submit a pull request.

## Known Bugs
### Major Bugs
No known bugs as of now.

### Minor Bugs
1. Camera framerate is slow on older phones.

# Contact Us
Daniel Pinson - pinsondg@dukes.jmu.edu
Vamsi Yadav - vamsiky@umich.edu

# Acknowledgements
All work was done as interns at [Radiant Solutions](http://www.radiantsolutions.com/) at the Charlottesville, Virginia branch.

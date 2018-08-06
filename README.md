# DefectDetect
This is an android app that identifies potholes on a road and marks their location on a map as well as providing a detailed summary.

## Getting Started
To download and run the app you need to get [android studio](https://developer.android.com/studio/). Then clone this repo to get the code and simply follow the instructions provided [here](https://developer.android.com/training/basics/firstapp/running-app). This will allow you to create either a virtual device or use your own physical device.

## App Features
This app uses a deep neural network called YOLO that is implemented on the Darknet framework. We trained the network on 1,800+ images in order to get accurate detections. This application has three different tabs: a map tab, a camera tab, and a list tab. The app automatically saves your data every time it is closed (but not if it crashes).
### Map Tab
This is where the application starts off when first opening it. It uses a google map to determine the user's location and show it on the map. Once a pothole is found, it will be displayed as a marker on the map. Clicking one of the markers will bring up more detailed information about the pothole in a text field at the bottom of the screen.
![alt text](https://github.com/pinsondg/DefectDetect/blob/master/screenshots/Screenshot_1533579507.png = 250x500)
### Camera Tab
The camera tab is where the magic of the app really happens. This brings up the phone's back camera and uses the neural network to draw a box around any pothole it finds. Note that the first time you open this tab, you will be asked to confirm if you want the current camera view. On some devices, the camera view is sideways. If this is your case, just click the rotate button in the upper left-hand corner to rotate the camera view. Once the phone finds a detection on the camera, you will be able to see the pothole appear on the map and in the list.
### List Tab
The list tab displays all the potholes your device has found in an ordered list. To delete an item from the list simply swipe left on an item and press the delete button that appears. To search for a specific pothole, just start typing in the search bar and the list will be modified. Taping on a list item will bring the user back to the map and to the marker of the pothole they selected. Taping the green 'X' button in the bottom righthand corner will prompt the user if they want to clear the list.
![alt text](https://github.com/pinsondg/DefectDetect/blob/master/screenshots/Screenshot_1533579541.png){:height="50%" width="50%"}

## Modifying Code
If you would like to modify the code, submit a pull request.

## Known Bugs
### Major Bugs
1. Due to the high CPU usage of a neural network, the app can crash on older phones.

### Minor Bugs
1. When swiping a list item, every i + 7 th item also swipes but hitting the delete button only deletes the item for that button.
2. Camera framerate is slow on older phones.

# Contact Us
Daniel Pinson - pinsondg@dukes.jmu.edu
Vamsi Yadav - vamsiky@umich.edu

# Acknowledgements
All work was done as interns at [Radiant Solutions](http://www.radiantsolutions.com/) at the Charlottesville, Virginia branch.

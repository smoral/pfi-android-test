package com.example.myfirstapp;

import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ImageUtils {
	
	/***
	 * http://stackoverflow.com/questions/15839316/cvresize-function-with-javacv
	 * @param originalImage
	 * @param width
	 * @param height
	 * @return resized image
	 */
	public static IplImage resizeImage(IplImage originalImage, int width, int height) {
		IplImage resized = IplImage.create(width, height, originalImage.depth(), originalImage.nChannels());
        //resize the image
        cvResize(originalImage,resized);
        return resized;
	}
	
	/***
	 * https://gist.github.com/kzudov/4967792
	 * @param originalImage
	 * @param rectangle
	 * @return cropped image
	 */
	public static IplImage cropImage(IplImage originalImage, CvRect rectangle) {
		//After setting ROI (Region-Of-Interest) all processing will only be done on the ROI
		cvSetImageROI(originalImage, rectangle);
		IplImage cropped = cvCreateImage(cvGetSize(originalImage), originalImage.depth(), originalImage.nChannels());
		//Copy original image (only ROI) to the cropped image
		cvCopy(originalImage, cropped);
		return cropped;
	}
}

package com.example.myfirstapp;
import static com.googlecode.javacv.cpp.opencv_core.CV_32SC1;
import static com.googlecode.javacv.cpp.opencv_core.CV_STORAGE_READ;
import static com.googlecode.javacv.cpp.opencv_core.cvAttrList;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
import static com.googlecode.javacv.cpp.opencv_core.cvOpenFileStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvReadByName;
import static com.googlecode.javacv.cpp.opencv_core.cvReadIntByName;
import static com.googlecode.javacv.cpp.opencv_core.cvReadStringByName;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseFileStorage;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_legacy.cvEigenDecomposite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.os.Environment;

import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacv.cpp.opencv_core.CvFileStorage;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class RecognitionService {
	
	final String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/facedata.xml"; // TODO: poner el path al archivo en android
	IplImage[] trainingFaceImgArr;
	private int nTrainFaces = 0;
	CvMat projectedTrainFaceMat;
	int nEigens = 0;
	int nPersons = 0;
	IplImage[] eigenVectArr;
	CvMat eigenValMat;
	IplImage pAvgTrainImg;
	final List<String> personNames = new ArrayList<String>();
	CvMat personNumTruthMat;
	IplImage[] testFaceImgArr;

	/**Finds the k most similar faces.*/
	public void recognize(final String testImagePath, final int k) {
		int i = 0;
		int nTestFaces = 0;
		CvMat trainPersonNumMat;
		float[] projectedTestFace;
		float confidence = 0.0f;

		testFaceImgArr = loadFaceImgArrayFromImagePath(testImagePath);
		nTestFaces = testFaceImgArr.length;
		trainPersonNumMat = loadTrainingData();
		if (trainPersonNumMat == null) {
			return;
		}
		projectedTestFace = new float[nEigens];

		for (i = 0; i < nTestFaces; i++) {
			int iNearest;
			int nearest;

			cvEigenDecomposite(testFaceImgArr[i], // obj
					nEigens, // nEigObjs
					new PointerPointer(eigenVectArr), // eigInput (Pointer)
					0, // ioFlags
					null, // userData
					pAvgTrainImg, // avg
					projectedTestFace); // coeffs

			final FloatPointer pConfidence = new FloatPointer(confidence);
			int [] knn = getKNN(projectedTestFace, new FloatPointer(
					pConfidence), k);
			iNearest = knn[0];//Tomo el primero. CAMBIAR!!
			
			confidence = pConfidence.get();
			nearest = trainPersonNumMat.data_i().get(iNearest);
			/*retornar un arreglo de legajos mas cercanos*/
			int [] nearestsLus = new int [knn.length];
			for(int j = 0 ; j < knn.length; j++){
				nearestsLus[j] = trainPersonNumMat.data_i().get(knn[j]);
			}
			System.out.println("Mas cercano: " + nearest);
			//return nearestsLus;
		}
	}

	private CvMat loadTrainingData() {
		CvMat pTrainPersonNumMat = null; // the person numbers during training
		CvFileStorage fileStorage;
		int i;
		fileStorage = cvOpenFileStorage(DATA_PATH, // filename
				null, // memstorage
				CV_STORAGE_READ, // flags
				null); // encoding
		if (fileStorage == null) {
			return null;
		}

		personNames.clear(); // Make sure it starts as empty.
		nPersons = cvReadIntByName(fileStorage, // fs
				null, // map
				"nPersons", // name
				0); // default_value
		if (nPersons == 0) {
			return null;
		}
		for (i = 0; i < nPersons; i++) {
			String sPersonName;
			String varname = "personName_" + (i + 1);
			sPersonName = cvReadStringByName(fileStorage, // fs
					null, // map
					varname, "");
			personNames.add(sPersonName);
		}
		nEigens = cvReadIntByName(fileStorage, // fs
				null, // map
				"nEigens", 0); // default_value
		nTrainFaces = cvReadIntByName(fileStorage, null, // map
				"nTrainFaces", 0); // default_value
		Pointer pointer = cvReadByName(fileStorage, // fs
				null, // map
				"trainPersonNumMat", // name
				cvAttrList()); // attributes
		pTrainPersonNumMat = new CvMat(pointer);

		pointer = cvReadByName(fileStorage, // fs
				null, // map
				"eigenValMat", // nmae
				cvAttrList()); // attributes
		eigenValMat = new CvMat(pointer);

		pointer = cvReadByName(fileStorage, // fs
				null, // map
				"projectedTrainFaceMat", // name
				cvAttrList()); // attributes
		projectedTrainFaceMat = new CvMat(pointer);

		pointer = cvReadByName(fileStorage, null, // map
				"avgTrainImg", cvAttrList()); // attributes
		pAvgTrainImg = new IplImage(pointer);

		eigenVectArr = new IplImage[nEigens];
		for (i = 0; i < nEigens; i++) {
			String varname = "eigenVect_" + i;
			pointer = cvReadByName(fileStorage, null, // map
					varname, cvAttrList()); // attributes
			eigenVectArr[i] = new IplImage(pointer);
		}
		cvReleaseFileStorage(fileStorage);
		
		return pTrainPersonNumMat;
	}

	private IplImage[] loadFaceImgArrayFromImagePath(final String imagePath) {
		IplImage[] faceImgArr;
		int iFace = 0;
		int nFaces = 1;
		int width = -1;
		int height = -1;
		nPersons = 0;

		faceImgArr = new IplImage[nFaces];
		personNumTruthMat = cvCreateMat(1, // rows
				nFaces, // cols
				CV_32SC1); // type, 32-bit unsigned, one channel

		for (int j1 = 0; j1 < nFaces; j1++) {
			personNumTruthMat.put(0, j1, 0);
		}
		nPersons = 1;
		int personNumber = 0;
		personNumTruthMat.put(0, // i
				iFace, // j
				personNumber); // v
		final IplImage faceImage = cvLoadImage(imagePath, // filename
				CV_LOAD_IMAGE_GRAYSCALE); // isColor
		if (faceImage == null) {
			throw new RuntimeException("Can't load image from " + imagePath);
		}
		if (width == -1) {
			width = faceImage.width();
			height = faceImage.height();
		} else if (faceImage.width() != width || faceImage.height() != height) {
			throw new RuntimeException("wrong size face in " + imagePath
					+ "\nwanted " + width + "x" + height + ", but found "
					+ faceImage.width() + "x" + faceImage.height());
		}
		faceImgArr[iFace] = faceImage;
		iFace++;		
		return faceImgArr;
	}
	/**Get the K nearest neighbors.*/
	private int [] getKNN(float projectedTestFace[],
			FloatPointer pConfidencePointer, int k) {
		double leastDistSq = Double.MAX_VALUE;
		int i = 0;
		int iNearest = 0;
		int [] nNearestIndexes = new int[k];
		Set<IndexDistance> indexDistances = new TreeSet<IndexDistance>();
		double [] distances = new double [nTrainFaces];
		
		for (int iTrain = 0; iTrain < nTrainFaces; iTrain++) {
			double distSq = 0;

			for (i = 0; i < nEigens; i++) {
				if (!Double.isNaN(projectedTestFace[i])) {
					float projectedTrainFaceDistance = (float) projectedTrainFaceMat
							.get(iTrain, i);
					float d_i = projectedTestFace[i]
							- projectedTrainFaceDistance;
					distSq += d_i * d_i;
				}
			}

			if (distSq < leastDistSq) {
				leastDistSq = distSq;
				iNearest = iTrain;
			}
			distances[iTrain] = distSq;
			indexDistances.add(new IndexDistance(iTrain, distSq));
			System.out.println("iTrain: " + iTrain + ", distance: " + distSq);
		}
		System.out.println("Least: " + iNearest + ", Distance: "+ leastDistSq);
		System.out.println("\n\nTreeSet\n\n");
		
		for(IndexDistance indexDistance : indexDistances){
			System.out.println("Index: " + indexDistance.getIndex()
					+ ", Distance: " + indexDistance.getDistance());
		}
		int j = 0;
		for(Iterator<IndexDistance> it = indexDistances.iterator(); it.hasNext() && j < k; j++){
			nNearestIndexes[j] = it.next().getIndex();
		}
		float pConfidence = (float) (1.0f - Math.sqrt(leastDistSq
				/ (float) (nTrainFaces * nEigens)) / 255.0f);
		pConfidencePointer.put(pConfidence);
		
		return nNearestIndexes;
	}
}

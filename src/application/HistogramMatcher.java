package application;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class HistogramMatcher {


	public static double histo(String path1) {
		try {
			Mat image0 = Imgcodecs.imread(path1, Imgcodecs.IMREAD_GRAYSCALE);

			// optimize the dimension of the loaded image
			Mat padded = optimizeImageDim(image0);

			padded.convertTo(padded, CvType.CV_32F);

			Mat hist0 = new Mat();

			int hist_bins = 30; // number of histogram bins
			int hist_range[] = { 0, 32 };// histogram range
			MatOfFloat ranges = new MatOfFloat(0f, 32f);
			MatOfInt histSize = new MatOfInt(25);
			//
			Imgproc.calcHist(Arrays.asList(image0), new MatOfInt(0), new Mat(), hist0, histSize, ranges);
			System.out.println(histSize);
			System.out.println(ranges);
			image0.release();
			return padded.hashCode();
		} catch (Exception e) {
			return 0;
		}
	}

	public static double compare(String path1, String path2) {
		try {
			Mat image0 = Imgcodecs.imread(path1, Imgcodecs.IMREAD_GRAYSCALE);
			Mat image1 = Imgcodecs.imread(path2, Imgcodecs.IMREAD_GRAYSCALE);

			// optimize the dimension of the loaded image
			Mat padded = optimizeImageDim(image0);
			padded.convertTo(padded, CvType.CV_32F);
			Mat padded1 = optimizeImageDim(image1);
			padded1.convertTo(padded1, CvType.CV_32F);

			Mat hist0 = new Mat();
			Mat hist1 = new Mat();

			int hist_bins = 30; // number of histogram bins
			int hist_range[] = { 0, 32 };// histogram range
			MatOfFloat ranges = new MatOfFloat(0f, 32f);
			MatOfInt histSize = new MatOfInt(25);
			//
			Imgproc.calcHist(Arrays.asList(image0), new MatOfInt(0), new Mat(), hist0, histSize, ranges);
			Imgproc.calcHist(Arrays.asList(image1), new MatOfInt(0), new Mat(), hist1, histSize, ranges);
			System.out.println(padded.hashCode());
			double res = Imgproc.compareHist(padded, padded1, Imgproc.CV_COMP_CORREL);
			System.out.println(res);
			image1.release();
			image0.release();
			return res;
		} catch (Exception e) {
			return 0;
		}
	}

	private static Mat optimizeImageDim(Mat image) {
		// init
		Mat padded = new Mat();
		// get the optimal rows size for dft
		int addPixelRows = Core.getOptimalDFTSize(image.rows());
		// get the optimal cols size for dft
		int addPixelCols = Core.getOptimalDFTSize(image.cols());
		// apply the optimal cols and rows size to the image
		Core.copyMakeBorder(image, padded, 0, addPixelRows - image.rows(), 0, addPixelCols - image.cols(),
				Core.BORDER_CONSTANT, Scalar.all(0));

		return padded;
	}

	private static Mat createOptimizedMagnitude(Mat complexImage) {
		// init
		List<Mat> newPlanes = new ArrayList<>();
		Mat mag = new Mat();
		// split the comples image in two planes
		Core.split(complexImage, newPlanes);
		// compute the magnitude
		Core.magnitude(newPlanes.get(0), newPlanes.get(1), mag);

		// move to a logarithmic scale
		Core.add(Mat.ones(mag.size(), CvType.CV_32F), mag, mag);
		Core.log(mag, mag);
		// optionally reorder the 4 quadrants of the magnitude image
		shiftDFT(mag);
		// normalize the magnitude image for the visualization since both JavaFX
		// and OpenCV need images with value between 0 and 255
		// convert back to CV_8UC1
		mag.convertTo(mag, CvType.CV_8UC1);
		Core.normalize(mag, mag, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);

		// you can also write on disk the resulting image...
		// Imgcodecs.imwrite("../magnitude.png", mag);

		return mag;
	}

	private static void shiftDFT(Mat image) {
		image = image.submat(new Rect(0, 0, image.cols() & -2, image.rows() & -2));
		int cx = image.cols() / 2;
		int cy = image.rows() / 2;

		Mat q0 = new Mat(image, new Rect(0, 0, cx, cy));
		Mat q1 = new Mat(image, new Rect(cx, 0, cx, cy));
		Mat q2 = new Mat(image, new Rect(0, cy, cx, cy));
		Mat q3 = new Mat(image, new Rect(cx, cy, cx, cy));

		Mat tmp = new Mat();
		q0.copyTo(tmp);
		q3.copyTo(q0);
		tmp.copyTo(q3);

		q1.copyTo(tmp);
		q2.copyTo(q1);
		tmp.copyTo(q2);
	}

	public static Mat bufferedImageToMat(BufferedImage image) {

		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		Mat mat = new Mat();
		mat.convertTo(mat, CvType.CV_8UC3);
		mat.put(0, 0, pixels);
		return mat;
	}

	public static double compare(BufferedImage i0, BufferedImage i1) {
		try {
			byte[] pixels0 = ((DataBufferByte) i0.getRaster().getDataBuffer()).getData();
			byte[] pixels1 = ((DataBufferByte) i1.getRaster().getDataBuffer()).getData();

			Mat image0 = new Mat();
			image0.convertTo(image0, CvType.CV_8UC3);
			image0.put(0, 0, pixels0);

			Mat image1 = new Mat();
			image1.convertTo(image1, CvType.CV_8UC3);
			image1.put(0, 0, pixels1);

			// optimize the dimension of the loaded image
			Mat padded = optimizeImageDim(image0);
			padded.convertTo(padded, CvType.CV_32F);
			Mat padded1 = optimizeImageDim(image1);
			padded1.convertTo(padded1, CvType.CV_32F);

			Mat hist0 = new Mat();
			Mat hist1 = new Mat();

			int hist_bins = 30; // number of histogram bins
			int hist_range[] = { 0, 32 };// histogram range
			MatOfFloat ranges = new MatOfFloat(0f, 32f);
			MatOfInt histSize = new MatOfInt(25);
			//
			Imgproc.calcHist(Arrays.asList(image0), new MatOfInt(0), new Mat(), hist0, histSize, ranges);
			Imgproc.calcHist(Arrays.asList(image1), new MatOfInt(0), new Mat(), hist1, histSize, ranges);
			double res = Imgproc.compareHist(padded, padded1, Imgproc.CV_COMP_CORREL);
			System.out.println("bf:" + res);
			image1.release();
			image0.release();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}


}

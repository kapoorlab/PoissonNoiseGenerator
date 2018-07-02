package poissonSimulator;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

public class InputOutputImageStack {

	
	public static RandomAccessibleInterval<FloatType> GenerateNoisyImage(final RandomAccessibleInterval<FloatType> inputimg, final int SNR) {
		
		final ImgFactory< FloatType > factory = Util.getArrayOrCellImgFactory( inputimg, new FloatType() );
		RandomAccessibleInterval<FloatType> noisylines = factory.create(inputimg, new FloatType());
		
		addBackground(Views.iterable(inputimg), 0.02);
		
		noisylines = Poissonprocess.poissonProcess(inputimg, SNR);
		
		
		return noisylines;
		
	}
	
	
	public static< T extends RealType< T > & NativeType< T >>  void addBackground(final IterableInterval<T> iterable, final double value) {
		for (final T t : iterable)
			t.setReal(t.getRealDouble() + value);
	}
	
	
	public static void main(String args[]) throws ImgIOException {
		
		new ImageJ();
		
		int SNR = 10;
		
		String folder = "/Users/aimachine/Documents/PairTrainingData/Low/";
		
		String basefolder = "/Users/aimachine/Documents/PairTrainingData/";
		
		RandomAccessibleInterval<FloatType> source = new ImgOpener().openImgs("/Users/aimachine/Documents/PairTrainingData/FileA_s1_t14.tif", new FloatType()).iterator().next();
		
		ImagePlus imp = ImageJFunctions.show(source);
		

		FileSaver fsB = new FileSaver(imp);
	
		fsB.saveAsTiff(basefolder + imp.getTitle());
		
		
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
		Normalize.normalize(Views.iterable(source), minval, maxval);
		
		
		
		RandomAccessibleInterval<FloatType> noisy = GenerateNoisyImage(source, SNR);
	
		
		
		
		
		ImagePlus impA = ImageJFunctions.show(noisy);
		
		FileSaver fs = new FileSaver(impA);
	
		fs.saveAsTiff(folder + imp.getTitle());
		
		
	}
	
}

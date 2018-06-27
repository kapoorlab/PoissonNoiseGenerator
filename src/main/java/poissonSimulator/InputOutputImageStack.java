package poissonSimulator;

import ij.ImageJ;
import ij.ImagePlus;
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
		
		addBackground(Views.iterable(inputimg), 0.2);
		
		noisylines = Poissonprocess.poissonProcess(inputimg, SNR);
		
		
		return noisylines;
		
	}
	
	
	public static< T extends RealType< T > & NativeType< T >>  void addBackground(final IterableInterval<T> iterable, final double value) {
		for (final T t : iterable)
			t.setReal(t.getRealDouble() + value);
	}
	
	
	public static void main(String args[]) throws ImgIOException {
		
		new ImageJ();
		
		int SNR = 30;
		RandomAccessibleInterval<FloatType> source = new ImgOpener().openImgs("/Users/aimachine/Documents/HighSNR/100x_bin2_05_1_w1CSU-TRIPLE-488_s7.TIF", new FloatType()).iterator().next();
		
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
		Normalize.normalize(Views.iterable(source), minval, maxval);
		
		ImagePlus imp = ImageJFunctions.show(source);
		
		RandomAccessibleInterval<FloatType> noisy = GenerateNoisyImage(source, SNR);
		Normalize.normalize(Views.iterable(noisy), minval, maxval);
		ImageJFunctions.show(noisy).setTitle(imp.getTitle());
	}
	
}

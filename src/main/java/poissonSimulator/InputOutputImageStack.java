package poissonSimulator;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;

import javax.swing.JFileChooser;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageConverter;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.Cursor;
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

	static ImagePlus imp;
	public static void SaveNoisyImages(RandomAccessibleInterval<FloatType> noisy, File savedirectory, String savename ) {
		
		
			imp = ImageJFunctions.show(noisy);

	
		
		FileSaver fsimp = new FileSaver(imp);
		
		fsimp.saveAsTiff(savedirectory + "/" + savename + ".tif");
		
		imp.close();
	}
	
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
	
	public static< T extends RealType< T > & NativeType< T >>  void subtractBackground(final IterableInterval<T> iterable, final double value) {
		for (final T t : iterable)
			t.setReal(t.getRealDouble() - value);
	}
	public static void main(String args[]) throws ImgIOException {
		
		
	
		ImageJ ins = new ImageJ();
		int SNR = 30;
		
		
		File SourceImages = new File(IJ.getDirectory("Choose Source Directory "));
		
		File TargetImages = new File(IJ.getDirectory("Choose Target Directory "));
		
		
		ImgOpener imgOpener = new ImgOpener();
		
		JFileChooser chooserImages = new JFileChooser();
		
		JFileChooser saveImages = new JFileChooser();
		
		
		
		chooserImages.setCurrentDirectory(SourceImages);
		saveImages.setCurrentDirectory(TargetImages);
		
		
		System.out.println("Files: " +  chooserImages.getCurrentDirectory().listFiles().length);
		
		File[] Images = chooserImages.getCurrentDirectory().listFiles();
		
		
		FloatType min = new FloatType(0);
		FloatType max = new FloatType(1);
		
		for (int i = 0; i < Images.length; ++i) {
			
			if(!Images[i].getName().contains("._"))
			{
			System.out.println("Noising File Number" + i);
			
			
			
			
			File Image = Images[i];
			System.out.println(Image.getAbsolutePath());
			String imagename = Image.getName().replaceFirst("[.][^.]+$", "");
			String savename =  "SNR" + Integer.toString(SNR) + imagename;
			RandomAccessibleInterval<FloatType> source = imgOpener.openImgs(Image.getAbsolutePath(), new FloatType())
					.get(0);
			Normalize.normalize(Views.iterable(source), min, max);
			RandomAccessibleInterval<FloatType> noisy = GenerateNoisyImage(source, SNR);
			Normalize.normalize(Views.iterable(noisy), min, max);
			SaveNoisyImages(noisy, TargetImages, savename);
			
		}
		}
		System.out.println("Done saving files");
		System.exit(1);
		
	}
	
}

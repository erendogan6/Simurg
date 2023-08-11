package com.eren.simurg;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import java.util.Random;

public class simurg extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture bird;
	Texture crashBird;
	Texture fallenBird;
	Texture lineBird;
	Texture upBird;
	float birdX;
	float birdY;
	int state= 0;
	float gravity = 0;
	final float gravityRatio = 0.3f;
	Texture[] bee;
	int currentBeeIndex;
	final int set=4;
	final float[] beeX = new float[set];
	float uzaklik;
	float ariHiz = 10;
	final float[] ari1Y = new float[set];
	final float[] ari2Y= new float[set];
	final float[] ari3Y= new float[set];
	Random random;
	Circle birdCircle;
	Circle[] beeCircle1;
	Circle[] beeCircle2;
	Circle[] beeCircle3;
	int score = 0;
	int gecis = 0;
	FreeTypeFontGenerator generator;
	FreeTypeFontGenerator.FreeTypeFontParameter parameter;
	FreeTypeFontGenerator.FreeTypeFontParameter parameter2;
	BitmapFont font2;
	BitmapFont font;
	BitmapFont font3;
	int overReason = 0;
	Sound sound ;
	int width;
	int height;
	int maxSkor;
	private static final String SCORE_FILE_PATH = "scores.txt";
	float elapsedTime;
	float switchInterval;
	@Override
	public void create () {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		batch = new SpriteBatch();
		background = new Texture("full-background.png");
		bird = new Texture("frame-1.png");
		crashBird = new Texture("hitframe-2.png");
		fallenBird = new Texture("hitframe-1.png");
		upBird = new Texture("frame-3.png");
		lineBird = new Texture("frame-2.png");
		birdX=(float)width/5;
		birdY=(float)height/2;
		bee = new Texture[2];
		bee[0] = new Texture("sprite1.png");
		bee[1] = new Texture("sprite2.png");
		currentBeeIndex = 0;
		uzaklik = (float)width/2;
		random = new Random();
		birdCircle = new Circle();
		beeCircle1 = new Circle[set];
		beeCircle2 = new Circle[set];
		beeCircle3 = new Circle[set];
		generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 65;
		parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.magFilter = Texture.TextureFilter.Linear;
		parameter.color = Color.RED;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth= 3;
		font2 = generator.generateFont(parameter);

		parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter2.size = 65;
		parameter2.minFilter = Texture.TextureFilter.Linear;
		parameter2.magFilter = Texture.TextureFilter.Linear;
		parameter2.color = Color.WHITE;
		parameter2.borderColor = Color.YELLOW;
		parameter2.borderWidth= 2;
		font = generator.generateFont(parameter2);
		font3 = generator.generateFont(parameter2);

		sound = Gdx.audio.newSound(Gdx.files.internal("flapping.mp3"));

		for (int i=0;i<set;i++){
			beeX[i]= width - (float)bee[currentBeeIndex].getWidth() / 2 + i*uzaklik;
			ari1Y[i] = random.nextInt(height - height / 5) + (float)height / 10;
			ari2Y[i] = random.nextInt(height - height / 5) + (float)height / 10;
			ari3Y[i] = random.nextInt(height - height / 5) + (float)height / 10;

			float[][] sets = {ari1Y, ari2Y, ari3Y};
			for (int n=0; n< sets.length;n++){
				for (int j = n+1; j < sets.length; j++) {
					if (Math.abs(sets[n][i] - sets[j][i]) <= ((float)height / 10)) {
						sets[n][i] = random.nextInt(height - height / 5) + (float)height / 10;
						n = -1; // İçteki döngüyü başa sar
						break; // İçteki döngüyü sonlandır
					}
				}
			}
			beeCircle1[i] = new Circle();
			beeCircle2[i] = new Circle();
			beeCircle3[i] = new Circle();
		}
		FileHandle fileHandle = Gdx.files.local(SCORE_FILE_PATH);
		if (fileHandle.exists()) {
			String scoreString = fileHandle.readString();
			maxSkor =Integer.parseInt(scoreString);
		}
		else{
			maxSkor=0;
		}
		elapsedTime = 0;
		switchInterval = 1.0f;
	}
	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,width ,height);
		if (state==1){
			if (beeX[gecis]<(float)width/5){
				score++;
				ariHiz += 0.1f;
				if (gecis<set-1){
					gecis++;
				}else{
					gecis=0;
				}
			}
			if (birdY>=height){
				birdY=height;
			}
			if (Gdx.input.justTouched() && birdY<height-(float)height/8 && birdY+10<height-(float)height/8){
				gravity=-9;
				sound.play();
			}
			for (int i =0;i<set;i++){
				if (beeX[i]<-35){
					beeX[i] = beeX[i] + set * uzaklik;
					ari1Y[i] = random.nextInt(height-height/5)+(float)height/10;
					ari2Y[i] = random.nextInt(height-height/5)+(float)height/10;
					ari3Y[i] = random.nextInt(height-height/5)+(float)height/10;
					float[][] sets = {ari1Y, ari2Y, ari3Y};
					for (int n=0; n< sets.length;n++){
						for (int j = n+1; j < sets.length; j++) {
							if (Math.abs(sets[n][i] - sets[j][i]) <= ((float)height / 10)) {
								sets[n][i] = random.nextInt(height - height / 5) + (float)height / 10;
								n = -1; // İçteki döngüyü başa sar
								break; // İçteki döngüyü sonlandır
							}
						}
					}
				}
				else {
					beeX[i]-=ariHiz;
				}

				elapsedTime += Gdx.graphics.getDeltaTime();

				if (elapsedTime >= switchInterval) {
					currentBeeIndex = (currentBeeIndex + 1) % 2;
					elapsedTime -= switchInterval;
				}
				batch.draw(bee[currentBeeIndex],
						beeX[i],
						ari1Y[i],
						(float)width/14,
						(float)height/10
				);
				batch.draw(bee[currentBeeIndex],
						beeX[i],
						ari2Y[i],
						(float)width/14,
						(float)height/10
				);
				batch.draw(bee[currentBeeIndex],
						beeX[i],
						ari3Y[i],
						(float)width/14,
						(float)height/10
				);
				beeCircle1[i] =new Circle(beeX[i]+(float)width/28,ari1Y[i]+(float)height/20,(float)height/24);
				beeCircle2[i] =new Circle(beeX[i]+(float)width/28,ari2Y[i]+(float)height/20,(float)height/24);
				beeCircle3[i] =new Circle(beeX[i]+(float)width/28,ari3Y[i]+(float)height/20,(float)height/24);
			}
			if (birdY>0) {
				gravity += gravityRatio;
				birdY = birdY - gravity;
			}else{
				state=2;
				overReason=1;
			}
		}
		else if (state==0) {
			if (Gdx.input.justTouched()){
				state=1;
			}
		} else if (state==2) {
			if (overReason==1) {
				batch.draw(fallenBird, birdX, birdY, (float)width / 12, (float)height / 10);
			}
			if (overReason==2) {
				batch.draw(crashBird, birdX, birdY, (float)width / 12, (float)height / 10);
			}
			if (score>maxSkor) {
				maxSkor=score;
				FileHandle fileHandle = Gdx.files.local(SCORE_FILE_PATH);
				fileHandle.writeString(Integer.toString(score), false);
			}
			font2.draw(batch,"Oyun Bitti. Tekrar Oynamak Için Dokunun",(float)width/6,(float)height/2+(float)height/4 );
			if (Gdx.input.justTouched()){
				state=1;
				birdY=(float)height / 2;
				gravity = -9;
				score =0;
				gecis=0;
				ariHiz=10;
				for (int i=0;i<set;i++){
					beeX[i]= width - (float)bee[currentBeeIndex].getWidth() / 2 + i*uzaklik;
					ari1Y[i] = random.nextInt(height - height / 5) + (float)height / 10;
					ari2Y[i] = random.nextInt(height - height / 5) + (float)height / 10;
					ari3Y[i] = random.nextInt(height - height / 5) + (float)height / 10;

					float[][] sets = {ari1Y, ari2Y, ari3Y};
					for (int n=0; n< sets.length;n++){
						for (int j = n+1; j < sets.length; j++) {
							if (Math.abs(sets[n][i] - sets[j][i]) <= ((float)height / 10)) {
								sets[n][i] = random.nextInt(height - height / 5) + (float)height / 10;
								n = -1; // İçteki döngüyü başa sar
								break; // İçteki döngüyü sonlandır
							}
						}
					}
					beeCircle1[i] = new Circle();
					beeCircle2[i] = new Circle();
					beeCircle3[i] = new Circle();
				}
			}
		}
		if (state!=2 && gravity>2) {
			batch.draw(bird, birdX, birdY, (float)width / 12, (float)height / 10);
		}
		else if (state!=2 && gravity<-2) {
			batch.draw(upBird,birdX,birdY,(float)width/12,(float)height/10);
		}
		else if (state != 2 && gravity >= -2) {
			batch.draw(lineBird,birdX,birdY,(float)width/12,(float)height/10);
		}
		font.draw(batch,"Skor:"+ score,(float)width/38,(float)height/15);
		font3.draw(batch,"Max Skor:"+maxSkor,(float)width/2+(float)width/4,(float)height/15);
		batch.end();
		birdCircle.set(birdX+(float)width/23,birdY+(float)height/20,(float)height/24);
		for ( int  i = 0 ; i < set ; i ++) {
			if (Intersector.overlaps(birdCircle,beeCircle1[i]) || Intersector.overlaps(birdCircle,beeCircle2[i]) || Intersector.overlaps(birdCircle,beeCircle3[i])){
				state=2;
				overReason=2;
			}
		}
	}
		@Override
	public void dispose () {
			sound.dispose();
			generator.dispose();
	}
}
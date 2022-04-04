;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
;
; Advanced Lawnmower Simulator By Sarah Cartwright (C)2002
;
; email: SarahCartwright@Yahoo.Com
;
;   web: HTTP://Sarah.Da.Ru
;
; Original idea found in 'Your Sinclair' many years ago!
; Submitted to Diverse Developments for their Lame Game competition:
;
; http://www.diversedev.com/
;
; Do what you want with the code, sound and graphics; they're freeware and open-source!
;
;
;
;
; A little note on development!
; This program was written in 2 days, after studying the Blitz Basic manual for 2 days.
; This is my first BB game. (Ball'Ball is coming soon, a Bat'n Ball game with a difference (orbiting bat, real physics collisions!) Ooooooooooo.... hehe)
; I'm not smart or anything, it's because I already know some of:
;
; Archemides BASIC
; Spectrum BASIC
; Z80 Assembler
; C / C++
; Java 
; Perl		( Check out my server sided web active OXO game, (IE 6 only sorry) at  HTTP://PerlOXO.Da.Ru  until June 2002.)
; ASP    
; JSP
; Prolog
;
; If you've never seen Blitz Basic before but know other languages, the only difference I can see between it and other BASICS (Beesides DX support
;  as well as graphics/sound/network tools :o) )
; is it's support for types!
;
; A 'Type' in BB can be thought of as a double point'erd linked list of 'Struct's in C++, or a Java Vector object containing global class data in
; an object without methods.


Graphics 640,480;.......................		Set Graphics mode!

Include "ImgFontInc.bb";................        Include the 'Proportional Image Font Handling Routines' (C) by Ian Duff (Yan) 2001 - yan@themutual.net
					   ;                        See the file 'ImgFontInc.bb' for his source and email.
 
Global FPS%;............................(INT)   This contains the frame rate the game is to run at.

Global CURRENT_GAME_PHASE%;.............(INT)   This is the games current phase, such as displaying the intro, playing the game, etc...

Global gameEngineRunning%;..............(FLAG)  This is the flag that controls whether the game is to continue looping or not.

Const  INTRO_PHASE%=1;..................(CONST) This is the ID for the intro phase, and is stored in CURRENT_GAME_PHASE.
Const  START_LEVEL%=2;..................(CONST) This is the ID for the level starting phase, and is stored in CURRENT_GAME_PHASE.
Const  PLAY_LEVEL%=3;...................(CONST) This is the ID for the playing level phase, and is stored in CURRENT_GAME_PHASE.
Const  END_LEVEL%=4;....................(CONST) This is the ID for the end of a level phase, and is stored in CURRENT_GAME_PHASE.

Global outcome%;........................(INT)   This holds the outcome of an ended level.
Const  WIN=1;...........................(CONST) This is stored in 'outcome' when a player cuts more than 30% of the grass, and then park in the shed.
Const  LOOSE=2;.........................(CONST) This is stored in 'outcome' when a player cuts less then 30% of the grass, and then park in the shed.
Const  CRASH=3;.........................(CONST) This is stored in 'outcome' when a player hits a stone when the mower is on.

Global mowerType;.......................(INT)   Mower type being used.
Global currentLevel%;...................(INT)   This holds the current level value, starting from 0.
Global cutGrassPercent%;................(INT)   This holds the amount of grass cut as a percentage when a level ends.

Global initOffsetY%;....................(INT)   This holds the Y offset position for the into's logo.
Global initScrollPosition%;.............(INT)   This holds the Y offset for the Instruction scroller offset.
Global initScrollDelay%;................(INT)   This is a delay counter to slow down the scroller by half.

Global gfxFullScreenCutGrass%;..........(INT)   This holds the handle to the display buffer that shows the cut grass.
Global gfxFullScreenGrass%;.............(INT)   This holds the handle to the display buffer that shows the long grass.
Global gfxStone%;.......................(INT)   This holds the handle to the stone graphic.
Global gfxStoneMask%;...................(INT)   This holds the handle to the stone graphic.
Global gfxGrass%;.......................(INT)   This holds the handle to the cut grass graphic mask, used for calculating cut grass.
Global gfxLawnMower1%;..................(INT)   This holds the handle to the unrotated lawn mower graphic.
Global gfxLawnMower2%;..................(INT)   This holds the handle to the unrotated lawn mower graphic.
Global gfxRotatedLawnMower%;............(INT)   This holds the handle to a temporary place to hold the rotated lawnmower graphic, before being stored in an array.
Global gfxShed%;........................(INT)   This holds the handle to the shed graphic.
Global gfxSheet;........................(INT)   This holds the handle to the buffer that the instructions are written onto.
Global gfxTitle;........................(INT)   This holds the handle to 'Lawnmower simulator' title.
Global gfxAdvanced;.....................(INT)   This holds the handle to 'Advanced' of the title.
Global gfxPlack;........................(INT)   This holds the handle to the background the instructions scroll over.
Global fontHandle%;.....................(INT)   This holds the handle to big font graphics.

Global sndMusic%;.......................(INT)   This holds the handle to the intro Midi music.
Global sndMowerStart%;..................(INT)   This holds the handle to the mower starting Wav.
Global sndMowerRuning%;.................(INT)   This holds the handle to the -looped- mower running Wav.
Global sndMowerOff%;....................(INT)   This holds the handle to the mower off Wav.
Global sndSparkle%;.....................(INT)   This holds the handle to the Win level Wav. (Grass cut 30% or more)
Global sndScream%;......................(INT)   This holds the handle to the Loose level Wav. (Grass cut less than 30%)
Global sndCrash%;.......................(INT)   This holds the handle to the mower hitting a stone Wav.
Global sndWalk%;........................(INT)   This holds the handle to Morris walking when the mower is off.

Global channelFX%;......................(INT)   This holds a handle to the current sound FX being played.
Global lastFX$;.........................(STR)   This holds the last sound effect to be played, to ensure sound synchronisation.

Global mowerAngle#;.....................(FLOAT) This holds the angle of the mower.
Global mowerX#;.........................(FLOAT) This is the mower's X position on the screen. (NOT the mower, but Morris's head)
Global mowerY#;.........................(FLOAT) This is the mower's Y position on the screen. (NOT the mower, but Morris's head)
Global motorOn%;........................(FLAG)  This flag holds the state of the mower engine. (On/Off) (True/False)
Global parkable%;.......................(FLAG)  This flag is true when Morris is inside the shed.
Global highestLevel%;...................(INT)   This holds the current highest level a player has reached before dying.

Type Stones;............................(TYPE)  This type holds any stones X/Y position.
	Field x%;...........................(INT)   The X position of a given stone.
	Field y%;...........................(INT)   The Y position of a given stone.
End Type;...............................        End the type.
Global stone.Stones;....................(-NEW-) Declare a new type of variable called a 'stone'.

Type RotatedMowers;.....................(TYPE)  This type holds information about the mower graphic. (360 are stored in an array later on)
	Field gfx%;.........................(INT)   This is the handle to the rotated mower.
	Field centerX%;.....................(INT)   This is the center X position of the mowers BLADE.
	Field centerY%;.....................(INT)   This is the center Y position of the mowers BLADE.
End Type;...............................        End the type.
Dim rotatedMower.RotatedMowers(2,360);..(-NEW-) Declare a new array of custom types called rotatedMower, to store pre-rendered rotated mower graphics
										;       and information regarding the location of the mower blade for each one.

Type Particles;.........................(TYPE)  This type holds information about a particle. (Used for the grass cutting/explosion effect)
	Field x#;...........................(FLOAT) The X position of the particle.
	Field y#;...........................(FLOAT) The Y position of the particle.
	Field ox#;..........................(FLOAT) The old X position of the particle.
	Field oy#;..........................(FLOAT) The old Y position of the particle.
	Field xv#;..........................(FLOAT) The X velocity position of the particle.
	Field yv#;..........................(FLOAT) The Y velocity position of the particle.
	Field age%;.........................(INT)   The age of the particle. (Anything over 50 dissapears)
	Field red%;.........................(INT)   The red intensity of the particle. (0-255)
	Field green%;.......................(INT)   The green intensity of the particle. (0-255)
	Field blue%;........................(INT)   The blue intensity of the particle. (0-255)
End Type;...............................        End the type.
Global particle.Particles;..............(-NEW-) Declare a new type of variable called a 'particle'

Dim instructions$(100);.................(STR)   Create an array to hold the lines of the instructions.
Global instructionLines;................(INT)   A place to hold the length of the instructions.



;Initialisation block.
;Here all the parts of the game are set up to their initial states.

Delay 5000			;Pause a while to allow screen mode to catch up.

initGameCore()		;Initialise the game core engine
initInstructions()	;Load the instructions
initGraphics()		;Load the graphics, and render mower and background
initIntro()			;Initialise the intro screen information
initActors()		;Initialise the Actor objects
initSound()			;Load the sounds

Delay 3000			;Pause a while before we start.

;Run the main game loop until ESC is pressed.
While gameEngineRunning : kickPhase() : Wend

;Free up the memory contained by the fonts.
free_img_font(fontHandle)

;End the program.
End

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Load the instructions from file, and store the number of lines they consist of.
;
Function initInstructions()
	Local fileHandle%
	Print "Initialising Instructions."
	instructionLines=0
	fileHandle=ReadFile("Instructions\Instructions.Txt")
	While Not Eof(fileHandle)	
		;Read each line of instructions into an array, keeping count of the number of lines read.
		instructions$(instructionLines)=ReadLine$(fileHandle)
		instructionLines=instructionLines+1
	Wend
	CloseFile(fileHandle)
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Load in the graphics, and render the mower.
;
Function initGraphics()
	Local angle%,filler%
	Print "Initialising Graphics."
	gfxStone=LoadImage("Graphics\Stone.bmp") : MidHandle gfxStone : MaskImage gfxStone,255,0,255
	gfxStoneMask=LoadImage("Graphics\StoneMask.bmp") : MidHandle gfxStoneMask : MaskImage gfxStoneMask,255,0,255
	gfxGrass=LoadImage("Graphics\Grass.bmp")
	gfxLawnMower1=LoadImage("Graphics\Mower1.bmp") : HandleImage gfxLawnMower1,15,50
	gfxLawnMower2=LoadImage("Graphics\Mower2.bmp") : HandleImage gfxLawnMower2,15,15
	gfxShed=LoadImage("Graphics\Shed.bmp")
	gfxTitle=LoadImage("Graphics\Title.bmp")
	gfxAdvanced=LoadImage("Graphics\Advanced.bmp")
	gfxPlack=LoadImage("Graphics\Plack.bmp")
	gfxSheet=CreateImage(380,153) : MaskImage gfxSheet,255,0,255
	gfxFullScreenGrass=CreateImage(640,480)
	gfxFullScreenCutGrass=CreateImage(640,480)
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Calculate the blades center for each rotation of the two mowers
;
Function initActors()
	Delay 1000
	SetBuffer FrontBuffer()
	Print:Print:Print:Print:Print:Print:Print:Print:Print:Print;We changed buffer, so print @ position is forgoten.
	Print "Rendering actors."
	Write "#1"
	;Create the Hovermower
	For angle=1 To 360 Step 10
		Write "."
		gfxRotatedLawnMower=CopyImage(gfxLawnMower1)
		RotateImage gfxRotatedLawnMower,angle+95
		For filler=angle To angle+9
			rotatedMower(0,filler)=New RotatedMowers
			rotatedMower(0,filler)\gfx=gfxRotatedLawnMower
			;This mower cuts 35 pixels from the mower origin
			rotatedMower(0,filler)\centerX=Cos(filler)*35
			rotatedMower(0,filler)\centerY=Sin(filler)*35
		Next
	Next
	Print
	Write "#2"
	;Create the Wheelymower
	For angle=1 To 360 Step 10
		Write "."
		gfxRotatedLawnMower=CopyImage(gfxLawnMower2)
		RotateImage gfxRotatedLawnMower,angle+95
		For filler=angle To angle+9
			rotatedMower(1,filler)=New RotatedMowers
			rotatedMower(1,filler)\gfx=gfxRotatedLawnMower
			;This mower cuts on the mower origin
			rotatedMower(1,filler)\centerX=0
			rotatedMower(1,filler)\centerY=0
		Next
	Next

	Print
	Print "Done."
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Load the sound effects.
;
Function initSound()
	Print "Initialising Sounds."
	sndMowerStart=LoadSound("Audio\MowerStart.wav")
	sndMowerRuning=LoadSound("Audio\MowerRuning.wav"):LoopSound(sndMowerRuning)
	sndMowerOff=LoadSound("Audio\MowerOff.wav")
	sndSparkle=LoadSound("Audio\Sparkle.wav")
	sndScream=LoadSound("Audio\Scream.wav")
	sndCrash=LoadSound("Audio\Crash.wav")
	sndWalk=LoadSound("Audio\Walk.wav"):LoopSound(sndWalk)
	Write "Analysing music file(This can take up to 2 mins for some sound cards)/"
	;Load and play music
	sndMusic=PlayMusic("Audio\Music.mid")
	Print "done."	
	Print "Starting game."
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Setup the initial game state, and do various program housekeeping tasks.
;
Function initGameCore()
	Color 255,255,255
	Print "Game written in Blitz-BASIC."
	Print
	Print "Entry for the Lame Game competition at Diverse Developments."
	Print "http://www.diversedev.com/
	Print
	Print "Log V 1.01"
	Print
	Write "Creating game core/"
	AppTitle "Advanced Lawnmower Simulator"
	HidePointer
	SeedRnd MilliSecs()
	fontHandle=load_img_font("ImgFonts\testfont.bmp")
	gameEngineRunning=True
	CURRENT_GAME_PHASE=INTRO_PHASE
	;Run game at 50 FPS
	FPS=CreateTimer(50)
	highestLevel=1
	mowerType=1
	Print "Bound to core."
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Initialise a new level ready to be played.
;
Function initLevel(level%)
	Local ctr%,stonesInLevel%
	stonesInLevel=Rnd(10,20)+level*20

	SetBuffer ImageBuffer(gfxFullScreenGrass)
	TileBlock gfxGrass,0,0
	
	SetBuffer ImageBuffer(gfxFullScreenCutGrass)
	Cls

	;Ensure mower attributes are ready for the start of a level
	cutGrassPercent=0
	mowerAngle=1
	mowerX=320
	mowerY=200
	motorOn=False
	parkable=False
	
	;Remove all currently existing stones and particles, then create new stones for level
	Delete Each Particles
	Delete Each Stones
	For ctr=1 To stonesInLevel
		stone=New Stones
		Repeat
			stone\x=Rnd(10,630)
			stone\y=Rnd(10,480)
		Until isASafePlace(stone)
	Next
		
	;Draw the stones to both buffers
	For stone=Each Stones
		SetBuffer ImageBuffer(gfxFullScreenGrass)
		DrawImage gfxStone,stone\x,stone\y
		SetBuffer ImageBuffer(gfxFullScreenCutGrass)
		DrawImage gfxStoneMask,stone\x,stone\y
	Next
	SetBuffer BackBuffer()
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Set the intro screen to it's initial state.
;
Function initIntro()
	initOffsetY=140
	initScrollPosition=0
	initScrollDelay=4
	SetBuffer ImageBuffer(gfxFullScreenGrass)
	TileBlock gfxGrass,0,0
	SetBuffer BackBuffer()
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;The main program branch. Every frame of play is said to be 'kicked'.
;
Function kickPhase()
	If KeyHit(1) Then gameEngineRunning=False:Return
	Select CURRENT_GAME_PHASE
	
		Case INTRO_PHASE 
			kickIntro()
			
		Case START_LEVEL
			kickStartLevel()
			
		Case PLAY_LEVEL
			kickPlayLevel()
			
		Case END_LEVEL
			kickEndLevel()

	End Select
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Perform housekeeping tasks between phases of the game.
;
Function kickNextPhase()
	Select CURRENT_GAME_PHASE
	
		Case INTRO_PHASE
			PauseChannel(sndMusic)
			If ChannelPlaying(channelFX) Then
				ChannelPan(channelFX,0)
				StopChannel(channelFX)
				EndIf
			lastFX=""
			currentLevel=0
			initLevel(currentLevel)			
			CURRENT_GAME_PHASE=START_LEVEL
			
		Case START_LEVEL
			If ChannelPlaying(channelFX) Then 
				StopChannel(channelFX)
				ChannelPan(channelFX,0)
				EndIf				
			MouseXSpeed() : MouseXSpeed()
			CURRENT_GAME_PHASE=PLAY_LEVEL
			
		Case PLAY_LEVEL
			If ChannelPlaying(channelFX) Then StopChannel(channelFX)
			ChannelPan(channelFX,0)			
			If outcome=WIN channelFX=PlaySound(sndSparkle)
			If outcome=LOOSE channelFX=PlaySound(sndScream)
			If outcome=CRASH channelFX=PlaySound(sndCrash)
			CURRENT_GAME_PHASE=END_LEVEL
			
		Case END_LEVEL
			If outcome=WIN Then
				currentLevel=currentLevel+1
				If currentLevel+1>highestLevel Then highestLevel=currentLevel+1
				initLevel(currentLevel)
				CURRENT_GAME_PHASE=START_LEVEL
				Else
				initIntro()			
				ResumeChannel(sndMusic)
				CURRENT_GAME_PHASE=INTRO_PHASE
				EndIf
				
	End Select
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Perform the intro phase.
;
Function kickIntro()
	DrawImage gfxFullScreenGrass,0,0
	drawShed()
	drawLogo()
	updateLogo()
	updateParticles()
	drawParticles()
	drawInstructions()
	WaitTimer(FPS)
	Flip		
	If KeyHit(2) Then mowerType=0:kickNextPhase()
	If KeyHit(3) Then mowerType=1:kickNextPhase()
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Perform the start level message phase.
;
Function kickStartLevel()
	drawField()
	drawMower()
	drawShed()
	draw_img_font(fontHandle,"Level "+(currentLevel+1),320,50,3,True,True,True)
	draw_img_font(fontHandle,"Cut 30% of the grass",320,100,3,True,True,True)
	draw_img_font(fontHandle,"then park to win!",320,150,3,True,True,True)
	draw_img_font(fontHandle,"You are using",320,250,3,True,True,True)
	Select(mowerType)
		Case 0	draw_img_font(fontHandle,"the Flymow!",320,300,3,True,True,True)	

		Case 1	draw_img_font(fontHandle,"the Wheelymow!",320,300,3,True,True,True)	
	End Select	
	draw_img_font(fontHandle,"Hit SPACE to Start!",320,390,3,True,True,True)	
	WaitTimer(FPS)
	Flip	
	If KeyHit(57) Then kickNextPhase()
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Perform the play level phase.
;
Function kickPlayLevel()
	getGameInput()
	If motorOn Then 
		cutGrass(mowerAngle)
		checkStoneColision()
		EndIf
	updateGameSound()
	updateParticles()
	drawField()
	drawMower()
	drawParticles()
	drawShed()
	checkParked()
	WaitTimer(FPS)
	Flip	
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Branch to one of two handlers for the ending phase, depending on the outcome of the level.
;
Function kickEndLevel()
	Select outcome

		Case WIN 
			kickEndWin()

		Case LOOSE
			kickEndNotEnoughGrass()

		Case CRASH
			kickEndHitStone()

	End Select	
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Perform the 'not enough grass cut' part of the end level phase.
;
Function kickEndNotEnoughGrass()
	updateParticles()
	drawField()
	drawMower()
	drawParticles()
	drawShed()
	draw_img_font(fontHandle,cutGrassPercent+"% Grass Cut!",320,50,3,True,True,True)
	draw_img_font(fontHandle,"Not enough grass cut!",320,100,3,True,True,True)
	draw_img_font(fontHandle,"GAME OVER!",320,150,3,True,True,True)
	draw_img_font(fontHandle,"Hit SPACE to Quit!",320,250,3,True,True,True)	
	WaitTimer(FPS)
	Flip	
	If KeyHit(57) Then kickNextPhase()
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Perform the 'hit stone' part of the end level phase.
;
Function kickEndHitStone()
	updateParticles()
	drawField()
	drawMower()
	drawParticles()
	drawShed()
	draw_img_font(fontHandle,"You crashed",320,50,3,True,True,True)
	draw_img_font(fontHandle,"into a stone!",320,100,3,True,True,True)
	draw_img_font(fontHandle,"GAME OVER!",320,150,3,True,True,True)
	draw_img_font(fontHandle,"Hit SPACE to Quit!",320,250,3,True,True,True)	
	WaitTimer(FPS)
	Flip	
	If KeyHit(57) Then kickNextPhase()
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Perform the 'win' part of the end level phase.
;
Function kickEndWin()
	updateParticles()
	drawField()
	drawMower()
	drawParticles()
	drawShed()
	draw_img_font(fontHandle,cutGrassPercent+"% Grass Cut!",320,50,3,True,True,True)
	draw_img_font(fontHandle,"Well Done, you made it!",320,100,3,True,True,True)
	draw_img_font(fontHandle,"Hit SPACE to Play!",320,250,3,True,True,True)	
	WaitTimer(FPS)
	Flip	
	If KeyHit(57) Then kickNextPhase()
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Get player input and update sound for the playing game phase.
;
Function getGameInput()
	If MouseHit(2) Then 
		motorOn=Not motorOn
		motorSoundChange()
		EndIf
	mowerAngle=mowerAngle+MouseXSpeed()/2
	MoveMouse 320,200
	If mowerAngle<1 Then mowerAngle=mowerAngle+360
	If mowerAngle>360 Then mowerAngle=mowerAngle-360
	If parkable And KeyHit(57) Then
		Repeat : Until Not KeyHit(57)
		cutGrassPercent=cutGrassPercent()
		If cutGrassPercent<30 Then 
			outcome=LOOSE
			Else
			outcome=WIN
			EndIf
		kickNextPhase()
		EndIf
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Draw the logo and grass clippings effect.
;
Function drawLogo()
	Local ran%,x%,y%
	SetBuffer ImageBuffer(gfxFullScreenGrass)
	Color 0,0,0
	ran=100+Rnd(0,155)
	x=130+Rnd(0,400)
	y=15+initOffsetY+Rnd(0,110)
	addParticles(x,y,5,0,ran,0)
	addParticles(x,y,5,ran-20,ran,0)			
	SetBuffer BackBuffer()
	DrawImage gfxTitle,120,15+initOffsetY
	DrawImage gfxAdvanced,145,15-initOffsetY
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Draw the instructions over the plack.
;
Function drawInstructions()
	Local ctr%
	DrawImage gfxPlack,95,150
	SetBuffer ImageBuffer(gfxSheet)
	Color 255,0,255
	Rect 0,0,ImageWidth(gfxSheet),ImageHeight(gfxSheet)
	Color 0,255,0
	For ctr=1 To 10
		Text 0,15-(initScrollPosition Mod 15)+(ctr*15),instructions(ctr+initScrollPosition/15)
	Next
	SetBuffer BackBuffer()
	DrawImageRect gfxSheet,130,163,0,30,400,123	
	draw_img_font(fontHandle,"Hit 1 or 2 to Start!",320,340,3,True,True,True)		
	draw_img_font(fontHandle,"Or ESC to Quit!",320,390,3,True,True,True)		
	draw_img_font(fontHandle,"Highest Level="+highestLevel,320,440,3,True,True,True)		
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Draw the playing field.
;
Function drawField()
	DrawImage gfxFullScreenCutGrass,0,0
	DrawImage gfxFullScreenGrass,0,0
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Draw Morris and his mower.
;
Function drawMower()
	DrawImage rotatedMower(mowerType,mowerAngle)\gfx,mowerX,mowerY
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Draw the shed.
;
Function drawShed()
	DrawImage gfxShed,220,380
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Draw all the visible particles.
;
Function drawParticles()
	Local r%,g%,b%
	For particle=Each Particles
		Color particle\red,particle\green,particle\blue
		Line particle\x,particle\y,particle\x+((particle\x-particle\ox)*2),particle\y+((particle\y-particle\oy)*2)
	Next
	Color 255,255,255
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Using the global variables 'mowerX' and 'mowerY', cut the grass patch that is at 'angle'.
;
Function cutGrass(angle%)
	Local ctr%,ang%,rad%
	
	addParticles(rotatedMower(mowerType,angle)\centerX+mowerX,rotatedMower(mowerType,angle)\centerY+mowerY,3,0,100+Rnd(0,155),0)
	
	Color 0,0,0
	SetBuffer ImageBuffer(gfxFullScreenGrass)
	Oval rotatedMower(mowerType,angle)\centerX+mowerX-12,rotatedMower(mowerType,angle)\centerY+mowerY-12,25,25
	
	Color 0,200-(Abs Sin(mowerAngle/2)*Float 100),0	
	SetBuffer ImageBuffer(gfxFullScreenCutGrass)
	Oval rotatedMower(mowerType,angle)\centerX+mowerX-12,rotatedMower(mowerType,angle)\centerY+mowerY-12,25,25
	
	For ctr=0 To 30
		Color 0,Rnd(0,100)+100,0
		ang=Rnd(0,359)
		rad=Rnd(1,20)
		Plot rotatedMower(mowerType,angle)\centerX+mowerX+(Cos(ang)*rad),rotatedMower(mowerType,angle)\centerY+mowerY+(Sin(ang)*rad)
		Next
	SetBuffer BackBuffer()
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Using pythagoras, work out the distance between the center of the mower blade, and the center of the stone.
;
Function checkStoneColision()
	Local bladeX%,bladeY%,distanceX,distanceY%,ran%
	bladeX=rotatedMower(mowerType,mowerAngle)\centerX+mowerX
	bladeY=rotatedMower(mowerType,mowerAngle)\centerY+mowerY
	For stone=Each Stones
		distanceX=bladeX-stone\x
		distanceY=bladeY-stone\y
		If Sqr((distanceX*distanceX)+(distanceY*distanceY))<25 Then 
			ran=100+Rnd(0,155)
			addParticles(bladeX,bladeY,100,ran,0,0)
			addParticles(bladeX,bladeY,100,ran-20,ran,0)			
			outcome=CRASH
			kickNextPhase()
			EndIf
	Next
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Using 'mowerAngle' update the mowers X/Y values to go foreward, keeping them inside the screen bounds.
;
Function moveMowerForeward()
	Local bladeX,bladeY
	mowerX=mowerX+(Cos(mowerAngle)*(mowerType+1))
	mowerY=mowerY+(Sin(mowerAngle)*(mowerType+1))
	
	bladeX=mowerX+rotatedMower(mowerType,mowerAngle)\centerX
	bladeY=mowerY+rotatedMower(mowerType,mowerAngle)\centerY
	
	If bladeX<20 Then mowerX=20-rotatedMower(mowerType,mowerAngle)\centerX
	If bladeX>620 Then mowerX=620-rotatedMower(mowerType,mowerAngle)\centerX
	If bladeY<20 Then mowerY=20-rotatedMower(mowerType,mowerAngle)\centerY
	If bladeY>460 Then mowerY=460-rotatedMower(mowerType,mowerAngle)\centerY
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Check to see if Morris is in the shed.
;
Function checkParked()
	If mowerX>220 And mowerX<420 And mowerY>380 Then
		draw_img_font(fontHandle,"Press Space",320,390,4,True,True,True)
		draw_img_font(fontHandle,"To go home!",320,440,4,True,True,True)
		parkable=True
		Else
		parkable=False
		EndIf
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Work out the amount of grass Morris has cut at the end of a level.
;
Function cutGrassPercent()
	Local x%,y%,cut%=0,total%=0,col1%,col2%

	SetBuffer ImageBuffer(gfxFullScreenCutGrass)
	Color 255,0,0
	Rect 220,380,200,200	

	SetBuffer FrontBuffer()
	For x=3 To 635 Step 4
		For y=3 To 475 Step 4		
			col1=ReadPixel(x,y,ImageBuffer(gfxFullScreenGrass))And $ffffff;Black is cut grass on this buffer
			col2=ReadPixel(x,y,ImageBuffer(gfxFullScreenCutGrass))And $ff0000;Red is the stone mask on this buffer
			If col2<>16711680 Then
				Color 0,255,0:Rect x,y,2,2
				total=total+1
				If col1=0 Then 
					cut=cut+1
					Color 255,255,255:Rect x,y,2,2
					EndIf
				Else
				Color 255,0,0:Rect x,y,2,2	
				End If
		Next
	Next	
	Delay 2000
	Return percent(cut,total)
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Returns the percentage of two values
;
Function percent(part%,total%)
	Local dec#,percent%
	dec=Float part/total
	dec=dec*100
	percent=dec
	Return percent
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Add a given number of particles to the particle pool.
;
Function addParticles(x%,y%,number%,r%,g%,b%)
	Local ctr%
	For ctr=0 To number
		particle=New Particles
		particle\x=x
		particle\y=y
		particle\ox=x
		particle\oy=y
		particle\xv=Rnd(-200,200)/100
		particle\yv=Rnd(-200,200)/100
		particle\age=Rnd(0,20)
		particle\red=r
		particle\green=g
		particle\blue=b
	Next
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Update the particles, and remove any old ones.
;
Function updateParticles()
	For particle=Each Particles
		particle\ox=particle\x
		particle\oy=particle\y

		particle\x=particle\x+particle\xv
		particle\y=particle\y+particle\yv
		
		particle\xv=particle\xv/1.01
		particle\yv=particle\yv/1.01;+0.1
		particle\age=particle\age+1

		If particle\age>=30 Delete particle
	Next
	
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Update the level playing sound FX's.
;
Function updateGameSound()
	If (Not ChannelPlaying(channelFX)) And (lastFX="start") Then 
		channelFX=PlaySound(sndMowerRuning)
		lastFX="runing"
		EndIf
	If MouseDown(1) Then 
		If (Not ChannelPlaying(channelFX)) And (lastFX="end" Or lastFX="" Or lastFX="silence") Then 
			channelFX=PlaySound(sndWalk)
			lastFX="walk"
			EndIf
		moveMowerForeward()
		Else
		If lastFX="walk" Then 
			StopChannel(channelFX)
			lastFX="silence"
			EndIf
		EndIf
	
	If ChannelPlaying(channelFX) Then ChannelPan(channelFX,mowerX/320-1)

End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Update the intro's logo and scroller.
;
Function updateLogo()
	If initOffsetY>0 Then initOffsetY=initOffsetY-1
	initScrollDelay=initScrollDelay-1
	If initScrollDelay=0 Then 
		initScrollDelay=4
		initScrollPosition=initScrollPosition+1
		If 10+initScrollPosition/15>instructionLines Then initScrollPosition=0
		EndIf
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Change the motor sound when the player toggles the mower's motor.
;
Function motorSoundChange()
	If motorOn;is motor on?
		;motor is now on, so
		If ChannelPlaying(channelFX) Then StopChannel(channelFX)
		channelFX=PlaySound(sndMowerStart);and play the start sound
		lastFX="start"
		Else
		;motor is off now.....
		If ChannelPlaying(channelFX) Then StopChannel(channelFX)
		channelFX=PlaySound(sndMowerOff);and play the start sound	
		lastFX="end"
		EndIf
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Check to see if a stone is not near the shed or the player, when they are getting placed before a level starts.
;
Function isASafePlace(stone.Stones)
	Local safeFromMower%,safeFromShed%
	safeFromMower=(((stone\x-mowerX)*(stone\x-mowerX))+((stone\y-mowerY)*(stone\y-mowerY)))>150
	safeFromShed=Not (stone\x>200 And stone\x<440 And stone\y>360)
	Return (safeFromMower And safeFromShed)
End Function
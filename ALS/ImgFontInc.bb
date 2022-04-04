;***********************************************************************
;***   			Proportional Image Font Handling Routines
;***		 Copyright Ian Duff (Yan) 2001 - yan@themutual.net
;***	If you use this in your code please keep this section intact
;***********************************************************************
;
Global img_fnt_img = 0
Global img_fnt_cnt = 0

;Call this function to load an image font BEFORE you use draw_img_font()
;This function returns a handle to the font bank and is used with the other routines
;usage example - 	myfont = load_img_font("afont.png")
;					myfont = load_img_font("adir\myfont.bmp")
;
Function load_img_font(file$)
	tmp_fle = ReadFile(Left$(file$, (Len(file$) - 4)) + ".dat")
	If (Not tmp_fle) Then RuntimeError "Couldn't Open '" + file$ + "' Data File!"
	msk_col = ReadInt(tmp_fle)
	bgn_ltr = ReadByte(tmp_fle)
	ltr_cnt = ReadByte(tmp_fle)
	fnt_h = ReadByte(tmp_fle)
	spc_w = ReadByte(tmp_fle)
	CloseFile(tmp_fle)
	fnt_bnk = CreateBank((ltr_cnt Shl 2) + 8)
	PokeInt fnt_bnk, 0, msk_col
	PokeByte fnt_bnk, 4, bgn_ltr
	PokeByte fnt_bnk, 5, ltr_cnt
	PokeByte fnt_bnk, 6, fnt_h
	PokeByte fnt_bnk, 7, spc_w	
	For c=0 To ltr_cnt - 1
		PokeInt fnt_bnk, (c Shl 2) + 8, LoadImage(Left$(file$, Len(file$) - 4) + "-" + c + Right$(file$, 4))
	Next
	If img_fnt_cnt > 0	;img_fnt_img
		If (fnt_h + 4) > ImageHeight(img_fnt_img)
			FreeImage img_fnt_img
			img_fnt_img = CreateImage(GraphicsWidth(), fnt_h + 4)
		EndIf		
	Else	
		img_fnt_img = CreateImage(GraphicsWidth(), fnt_h + 4)
	EndIf
	img_fnt_cnt = img_fnt_cnt + 1
	Return fnt_bnk 
End Function

;Call this function to delete an image font and free the memory it used
;usage example - 	free_img_font(myfont)
;
Function free_img_font(fnt_bnk)
	ltr_cnt = PeekByte(fnt_bnk, 5) - 1
	For c=0 To ltr_cnt
		FreeImage PeekInt(fnt_bnk, (c Shl 2) + 8)
	Next
	FreeBank fnt_bnk
	img_fnt_cnt = img_fnt_cnt - 1
	If img_fnt_cnt = 0 Then FreeImage img_fnt_img
End Function

;Use this function to print your text 
;usage example - 	draw_img_font(myfont, "abcde", screenx, screeny, 4, 1, 1, 1)
;					draw_img_font(myfont, text$, screenx, screeny, 8, 0, 0, 0)
; 
Function draw_img_font(fnt_bnk, display$, x, y, spc, wobble = 0, centrex = 0, centrey = 0)
	xinc = 0
	tmp_bffr = GraphicsBuffer()
	;tmp_col = ReadPixel(0, 0) And $ffffff ; Uncomment this (and bottom) to keep clscolor (kind'a) intact (very slow though)
	msk_col = PeekInt(fnt_bnk, 0)
	bgn_ltr = PeekByte(fnt_bnk, 4)
	ltr_cnt = PeekByte(fnt_bnk, 5)
	fnt_h = PeekByte(fnt_bnk, 6)
	spc_w = PeekByte(fnt_bnk, 7)
	SetBuffer ImageBuffer(img_fnt_img)
	ClsColor (msk_col And $ff0000)Shr 16, (msk_col And $ff00)Shr 8, msk_col And $ff 
	Cls	
	For t=1 To Len(display$)
		fnt_chr = Asc(Mid$(display$,t,1))
		If fnt_chr > 32
			If fnt_chr <= (ltr_cnt + 32)
				fnt_chr = (fnt_chr - bgn_ltr)
				fnt_img = PeekInt(fnt_bnk, (fnt_chr Shl 2) + 8)
				DrawBlock fnt_img, xinc, 2 + ((Sin(MilliSecs() + (t Shl 5) + y) * 2) * wobble)
				xinc = xinc + ImageWidth(fnt_img)
			Else
				RuntimeError "The character '" + Chr$(fnt_chr) + "' is not supported by this Image Font"
			EndIf
		Else
			xinc = xinc + spc_w
		EndIf
		xinc = xinc + spc
	Next
	xinc = xinc - spc
	SetBuffer tmp_bffr
	MaskImage img_fnt_img, (msk_col And $ff0000)Shr 16, (msk_col And $ff00)Shr 8, msk_col And $ff
	DrawImageRect img_fnt_img, x - ((xinc Shr 1) * centrex), y - ((fnt_h Shr 1) * centrey), 0, 0, xinc, fnt_h + 4
	;ClsColor (tmp_col And $ff0000)Shr 16, (tmp_col And $ff00)Shr 8, tmp_col And $ff ; And this (see above)
End Function
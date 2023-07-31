package com.mobiai.app.ultils

import android.content.Context
import com.mobiai.R
import com.mobiai.base.basecode.language.Language

class LanguageProvider(val context: Context) {

    val listSettingLanguage = mutableListOf(
        Language(R.drawable.flag_en, context.getString(R.string.language_english), "en"),
        Language(
            R.drawable.flag_es_spain,
            context.getString(R.string.language_spain),
            "es"
        ),

        Language(
            R.drawable.flag_pt_portugal,
            context.getString(R.string.language_portugal),
            "pt"
        ),

        Language(
            R.drawable.flag_fr_france,
            context.getString(R.string.language_france),
            "fr"
        ),

         Language(R.drawable.flag_vn_vietnam, context.getString(R.string.language_vietnamese), "vi"),

        Language(R.drawable.flag_ko_korean,
          context.getString(R.string.language_korean),
            "ko"),

        Language(R.drawable.flag_hi_hindi,
          context.getString(R.string.language_hindi),
            "hi"),

        Language(R.drawable.flag_lr_liberia,
          context.getString(R.string.language_liberia),
            "lr"),

        Language(R.drawable.flag_it_italy,
          context.getString(R.string.language_italian),
            "it"),

        Language(R.drawable.flag_jp_japan,
          context.getString(R.string.language_japan),
            "ja"),

        Language(R.drawable.flag_gr_greece,
          context.getString(R.string.language_greece),
            "gr"),

    )
    val listFirstOpenLanguage = mutableListOf(
        Language(R.drawable.flag_en, context.getString(R.string.language_english), "en"),
        Language(
            R.drawable.flag_es_spain,
            context.getString(R.string.language_spain),
            "es"
        ),

        Language(
            R.drawable.flag_pt_portugal,
            context.getString(R.string.language_portugal),
            "pt"
        ),

        Language(
            R.drawable.flag_fr_france,
            context.getString(R.string.language_france),
            "fr"
        ),
        Language(
            R.drawable.flag_de_germany,
            context.getString(R.string.language_germany),
            "de"
        ),

    )


}
package com.example.dogapp.ui.components

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.doAfterTextChanged
import com.example.dogapp.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

@Composable
fun BreedField(
    value: String,
    onValueChange: (String) -> Unit,
    names: List<String>,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val updateValue by rememberUpdatedState(onValueChange)
    val adapter = remember(context, names) { BreedAdapter(context, names) }

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            val materialContext = ContextThemeWrapper(
                viewContext,
                com.google.android.material.R.style.Theme_Material3_Light_NoActionBar
            )
            val layout = LayoutInflater.from(materialContext)
                .inflate(R.layout.breed_field, null) as TextInputLayout
            val input = layout.editText as MaterialAutoCompleteTextView
            input.doAfterTextChanged { updateValue(it.toString()) }
            layout
        },
        update = { layout ->
            val input = layout.editText as MaterialAutoCompleteTextView
            layout.isEnabled = enabled
            if (input.text.toString() != value) input.setText(value, false)
            if (input.adapter !== adapter) {
                input.setAdapter(adapter)
                if (input.hasFocus() && input.enoughToFilter()) input.refreshAutoCompleteResults()
            }
        }
    )
}

private class BreedAdapter(context: Context, private val names: List<String>) :
    ArrayAdapter<String>(context, R.layout.item_breed_dropdown) {

    private val prefixFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val prefix = constraint?.toString()?.trim().orEmpty()
            val matches = if (prefix.length < 2) emptyList() else {
                names.filter { it.startsWith(prefix, ignoreCase = true) }
            }
            return FilterResults().apply {
                values = matches
                count = matches.size
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            @Suppress("UNCHECKED_CAST")
            val matches = results.values as List<String>
            setNotifyOnChange(false)
            clear()
            addAll(matches)
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter = prefixFilter
}

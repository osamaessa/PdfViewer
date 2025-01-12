package com.osama.pdfviewer

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import java.io.File

class PdfViewer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private lateinit var pdfRendererCore: PdfRendererCore
    private lateinit var pdfViewAdapter: PdfViewAdapter
    private var pdfRendererCoreInitialised = false
    private var pageMargin: Rect = Rect(0,0,0,0)
    lateinit var recyclerView: RecyclerView
    private var divider: Drawable? = null
    private var restoredScrollPosition: Int = NO_POSITION
    private var postInitializationAction: (() -> Unit)? = null
    private var positionToUseForState: Int = 0

    fun init(file: File, onPdfLoaded: () -> Unit) {
        val fileDescriptor = PdfRendererCore.getFileDescriptor(file)
        init(fileDescriptor, onPdfLoaded)
    }


    private fun init(fileDescriptor: ParcelFileDescriptor, onPdfLoaded: () -> Unit) {
        // Proceed with safeFile
        pdfRendererCore = PdfRendererCore(context, fileDescriptor)
        pdfRendererCoreInitialised = true
        pdfViewAdapter = PdfViewAdapter(context,pdfRendererCore, pageMargin)
        val v = LayoutInflater.from(context).inflate(R.layout.layout_pdf_viewer, this, false)
        addView(v)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.apply {
            adapter = pdfViewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                divider?.let { setDrawable(it) }
            }.let { addItemDecoration(it) }
            addOnScrollListener(scrollListener)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (restoredScrollPosition != NO_POSITION) {
                recyclerView.scrollToPosition(restoredScrollPosition)
                restoredScrollPosition = NO_POSITION
            }
        }, 500)

        recyclerView.post {
            postInitializationAction?.invoke()
            postInitializationAction = null
            onPdfLoaded.invoke()
        }

    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        private var lastFirstVisiblePosition = NO_POSITION
        private var lastCompletelyVisiblePosition = NO_POSITION

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            val isPositionChanged = firstVisiblePosition != lastFirstVisiblePosition ||
                    firstCompletelyVisiblePosition != lastCompletelyVisiblePosition
            if (isPositionChanged) {
                val positionToUse = if (firstCompletelyVisiblePosition != NO_POSITION) {
                    firstCompletelyVisiblePosition
                } else {
                    firstVisiblePosition
                }
                positionToUseForState = positionToUse
                lastFirstVisiblePosition = firstVisiblePosition
                lastCompletelyVisiblePosition = firstCompletelyVisiblePosition
            }else{
                positionToUseForState = firstVisiblePosition
            }
        }
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

        }
    }

}
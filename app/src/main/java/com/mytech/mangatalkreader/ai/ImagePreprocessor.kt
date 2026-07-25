package com.mytech.mangatalkreader.ai

import android.graphics.Bitmap
import android.graphics.Color

object ImagePreprocessor {

    fun enhanceForManga(src: Bitmap, contrast: Float = 1.6f, blurRadius: Int = 1): Bitmap {
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        applyGrayscale(pixels)
        if (contrast != 1.0f) {
            applyContrast(pixels, contrast)
        }
        if (blurRadius > 0) {
            applyFastBlur(pixels, width, height, blurRadius)
        }
        applyAdaptiveThreshold(pixels)

        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    private fun applyGrayscale(pixels: IntArray) {
        for (i in pixels.indices) {
            val p = pixels[i]
            val gray = (Color.red(p) * 0.299f + Color.green(p) * 0.587f + Color.blue(p) * 0.114f).toInt()
            pixels[i] = Color.rgb(gray, gray, gray)
        }
    }

    private fun applyContrast(pixels: IntArray, contrast: Float) {
        for (i in pixels.indices) {
            val newGray = ((Color.red(pixels[i]) - 128) * contrast + 128).toInt().coerceIn(0, 255)
            pixels[i] = Color.rgb(newGray, newGray, newGray)
        }
    }

    private fun applyFastBlur(pixels: IntArray, w: Int, h: Int, radius: Int) {
        if (w < radius * 2 + 1 || h < radius * 2 + 1) return
        val temp = IntArray(pixels.size)

        for (x in 0 until w) {
            var sum = 0
            for (yInit in 0..radius) {
                sum += Color.red(pixels[yInit * w + x])
            }
            for (y in 0 until h) {
                val addIdx = (y + radius).coerceAtMost(h - 1) * w + x
                sum += Color.red(pixels[addIdx])
                if (y > radius) {
                    val subIdx = (y - radius - 1) * w + x
                    sum -= Color.red(pixels[subIdx])
                }
                val avg = sum / (radius * 2 + 1)
                temp[y * w + x] = Color.rgb(avg, avg, avg)
            }
        }

        for (y in 0 until h) {
            var sum = 0
            for (xInit in 0..radius) {
                sum += Color.red(temp[y * w + xInit])
            }
            for (x in 0 until w) {
                val addIdx = y * w + (x + radius).coerceAtMost(w - 1)
                sum += Color.red(temp[addIdx])
                if (x > radius) {
                    val subIdx = y * w + (x - radius - 1).coerceAtLeast(0)
                    sum -= Color.red(temp[subIdx])
                }
                val avg = sum / (radius * 2 + 1)
                pixels[y * w + x] = Color.rgb(avg, avg, avg)
            }
        }
    }

    private fun applyAdaptiveThreshold(pixels: IntArray) {
        var sum: Long = 0
        for (p in pixels) {
            sum += Color.red(p).toLong()
        }
        val threshold = (sum / pixels.size).toInt() - 20

        for (i in pixels.indices) {
            val binary = if (Color.red(pixels[i]) > threshold) 255 else 0
            pixels[i] = Color.rgb(binary, binary, binary)
        }
    }
}

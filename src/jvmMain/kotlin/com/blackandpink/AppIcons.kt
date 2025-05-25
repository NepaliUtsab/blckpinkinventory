package com.blackandpink

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Custom icon set for application-specific icons
 */
object AppIcons {
    // Inventory icon
    val Inventory: ImageVector
        get() {
            if (_inventory == null) {
                _inventory = materialIcon(name = "Inventory") {
                    materialPath {
                        moveTo(20.0f, 3.0f)
                        horizontalLineTo(4.0f)
                        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                        verticalLineToRelative(14.0f)
                        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                        horizontalLineToRelative(16.0f)
                        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                        verticalLineTo(5.0f)
                        curveTo(22.0f, 3.9f, 21.1f, 3.0f, 20.0f, 3.0f)
                        close()
                        moveTo(10.0f, 17.0f)
                        horizontalLineTo(6.0f)
                        verticalLineTo(7.0f)
                        horizontalLineToRelative(4.0f)
                        verticalLineTo(17.0f)
                        close()
                        moveTo(18.0f, 17.0f)
                        horizontalLineToRelative(-6.0f)
                        verticalLineTo(7.0f)
                        horizontalLineToRelative(6.0f)
                        verticalLineTo(17.0f)
                        close()
                    }
                }
            }
            return _inventory!!
        }
    private var _inventory: ImageVector? = null

    // Receipt icon
    val Receipt: ImageVector
        get() {
            if (_receipt == null) {
                _receipt = materialIcon(name = "Receipt") {
                    materialPath {
                        moveTo(18.0f, 17.0f)
                        horizontalLineTo(6.0f)
                        verticalLineToRelative(-2.0f)
                        horizontalLineToRelative(12.0f)
                        verticalLineToRelative(2.0f)
                        close()
                        moveTo(18.0f, 13.0f)
                        horizontalLineTo(6.0f)
                        verticalLineToRelative(-2.0f)
                        horizontalLineToRelative(12.0f)
                        verticalLineToRelative(2.0f)
                        close()
                        moveTo(18.0f, 9.0f)
                        horizontalLineTo(6.0f)
                        verticalLineTo(7.0f)
                        horizontalLineToRelative(12.0f)
                        verticalLineToRelative(2.0f)
                        close()
                        moveTo(19.0f, 3.0f)
                        horizontalLineToRelative(-1.0f)
                        horizontalLineTo(6.0f)
                        curveTo(4.9f, 3.0f, 4.0f, 3.9f, 4.0f, 5.0f)
                        verticalLineToRelative(14.0f)
                        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                        horizontalLineToRelative(12.0f)
                        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                        verticalLineToRelative(-1.0f)
                        horizontalLineTo(19.0f)
                        verticalLineToRelative(1.0f)
                        curveToRelative(0.0f, 0.55f, -0.45f, 1.0f, -1.0f, 1.0f)
                        horizontalLineTo(6.0f)
                        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
                        verticalLineTo(5.0f)
                        curveToRelative(0.0f, -0.55f, 0.45f, -1.0f, 1.0f, -1.0f)
                        horizontalLineToRelative(12.0f)
                        curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
                        verticalLineToRelative(1.0f)
                        horizontalLineToRelative(1.0f)
                        verticalLineTo(5.0f)
                        curveTo(20.0f, 3.9f, 19.1f, 3.0f, 18.0f, 3.0f)
                        close()
                    }
                }
            }
            return _receipt!!
        }
    private var _receipt: ImageVector? = null
    
    // BarChart icon
    val BarChart: ImageVector
        get() {
            if (_barChart == null) {
                _barChart = materialIcon(name = "BarChart") {
                    materialPath {
                        moveTo(5.0f, 9.2f)
                        horizontalLineToRelative(3.0f)
                        verticalLineToRelative(10.0f)
                        horizontalLineTo(5.0f)
                        verticalLineTo(9.2f)
                        close()
                        moveTo(10.6f, 5.0f)
                        horizontalLineToRelative(2.8f)
                        verticalLineToRelative(14.0f)
                        horizontalLineToRelative(-2.8f)
                        verticalLineTo(5.0f)
                        close()
                        moveTo(16.2f, 13.0f)
                        horizontalLineTo(19.0f)
                        verticalLineToRelative(6.0f)
                        horizontalLineToRelative(-2.8f)
                        verticalLineTo(13.0f)
                        close()
                    }
                }
            }
            return _barChart!!
        }
    private var _barChart: ImageVector? = null
    
    // Upload icon
    val Upload: ImageVector
        get() {
            if (_upload == null) {
                _upload = materialIcon(name = "Upload") {
                    materialPath {
                        moveTo(5.0f, 20.0f)
                        horizontalLineTo(19.0f)
                        verticalLineTo(18.0f)
                        horizontalLineTo(5.0f)
                        verticalLineTo(20.0f)
                        close()
                        moveTo(12.0f, 2.0f)
                        lineTo(6.0f, 8.0f)
                        horizontalLineTo(10.0f)
                        verticalLineTo(16.0f)
                        horizontalLineToRelative(4.0f)
                        verticalLineTo(8.0f)
                        horizontalLineTo(18.0f)
                        lineTo(12.0f, 2.0f)
                        close()
                    }
                }
            }
            return _upload!!
        }
    private var _upload: ImageVector? = null
    
    // Download icon
    val Download: ImageVector
        get() {
            if (_download == null) {
                _download = materialIcon(name = "Download") {
                    materialPath {
                        moveTo(5.0f, 20.0f)
                        horizontalLineTo(19.0f)
                        verticalLineTo(18.0f)
                        horizontalLineTo(5.0f)
                        verticalLineTo(20.0f)
                        close()
                        moveTo(12.0f, 16.0f)
                        lineTo(18.0f, 10.0f)
                        horizontalLineTo(14.0f)
                        verticalLineTo(2.0f)
                        horizontalLineToRelative(-4.0f)
                        verticalLineTo(10.0f)
                        horizontalLineTo(6.0f)
                        lineTo(12.0f, 16.0f)
                        close()
                    }
                }
            }
            return _download!!
        }
    private var _download: ImageVector? = null
    
    // Remove icon
    val Remove: ImageVector
        get() {
            if (_remove == null) {
                _remove = materialIcon(name = "Remove") {
                    materialPath {
                        moveTo(7.0f, 11.0f)
                        verticalLineToRelative(2.0f)
                        horizontalLineToRelative(10.0f)
                        verticalLineToRelative(-2.0f)
                        horizontalLineTo(7.0f)
                        close()
                    }
                }
            }
            return _remove!!
        }
    private var _remove: ImageVector? = null
    
    // List view icon
    val List: ImageVector
        get() {
            if (_list == null) {
                _list = materialIcon(name = "List") {
                    materialPath {
                        moveTo(3.0f, 13.0f)
                        horizontalLineToRelative(2.0f)
                        verticalLineToRelative(-2.0f)
                        horizontalLineTo(3.0f)
                        verticalLineToRelative(2.0f)
                        close()
                        moveTo(3.0f, 17.0f)
                        horizontalLineToRelative(2.0f)
                        verticalLineToRelative(-2.0f)
                        horizontalLineTo(3.0f)
                        verticalLineToRelative(2.0f)
                        close()
                        moveTo(3.0f, 9.0f)
                        horizontalLineToRelative(2.0f)
                        verticalLineTo(7.0f)
                        horizontalLineTo(3.0f)
                        verticalLineToRelative(2.0f)
                        close()
                        moveTo(7.0f, 13.0f)
                        horizontalLineToRelative(14.0f)
                        verticalLineToRelative(-2.0f)
                        horizontalLineTo(7.0f)
                        verticalLineToRelative(2.0f)
                        close()
                        moveTo(7.0f, 17.0f)
                        horizontalLineToRelative(14.0f)
                        verticalLineToRelative(-2.0f)
                        horizontalLineTo(7.0f)
                        verticalLineToRelative(2.0f)
                        close()
                        moveTo(7.0f, 7.0f)
                        verticalLineToRelative(2.0f)
                        horizontalLineToRelative(14.0f)
                        verticalLineTo(7.0f)
                        horizontalLineTo(7.0f)
                        close()
                    }
                }
            }
            return _list!!
        }
    private var _list: ImageVector? = null
    
    // Grid view icon
    val Grid: ImageVector
        get() {
            if (_grid == null) {
                _grid = materialIcon(name = "Grid") {
                    materialPath {
                        moveTo(4.0f, 11.0f)
                        horizontalLineToRelative(5.0f)
                        verticalLineTo(5.0f)
                        horizontalLineTo(4.0f)
                        verticalLineToRelative(6.0f)
                        close()
                        moveTo(4.0f, 18.0f)
                        horizontalLineToRelative(5.0f)
                        verticalLineToRelative(-6.0f)
                        horizontalLineTo(4.0f)
                        verticalLineToRelative(6.0f)
                        close()
                        moveTo(10.0f, 18.0f)
                        horizontalLineToRelative(5.0f)
                        verticalLineToRelative(-6.0f)
                        horizontalLineToRelative(-5.0f)
                        verticalLineToRelative(6.0f)
                        close()
                        moveTo(15.0f, 11.0f)
                        horizontalLineToRelative(5.0f)
                        verticalLineTo(5.0f)
                        horizontalLineToRelative(-5.0f)
                        verticalLineToRelative(6.0f)
                        close()
                        moveTo(10.0f, 5.0f)
                        verticalLineToRelative(6.0f)
                        horizontalLineToRelative(5.0f)
                        verticalLineTo(5.0f)
                        horizontalLineToRelative(-5.0f)
                        close()
                        moveTo(15.0f, 12.0f)
                        horizontalLineToRelative(5.0f)
                        verticalLineToRelative(6.0f)
                        horizontalLineToRelative(-5.0f)
                        verticalLineToRelative(-6.0f)
                        close()
                    }
                }
            }
            return _grid!!
        }
    private var _grid: ImageVector? = null
    
    // Filter icon
    val Filter: ImageVector
        get() {
            if (_filter == null) {
                _filter = materialIcon(name = "Filter") {
                    materialPath {
                        moveTo(10.0f, 18.0f)
                        horizontalLineToRelative(4.0f)
                        verticalLineToRelative(-2.0f)
                        horizontalLineToRelative(-4.0f)
                        verticalLineToRelative(2.0f)
                        close()
                        moveTo(3.0f, 6.0f)
                        verticalLineToRelative(2.0f)
                        horizontalLineToRelative(18.0f)
                        verticalLineTo(6.0f)
                        horizontalLineTo(3.0f)
                        close()
                        moveTo(6.0f, 13.0f)
                        horizontalLineToRelative(12.0f)
                        verticalLineToRelative(-2.0f)
                        horizontalLineTo(6.0f)
                        verticalLineToRelative(2.0f)
                        close()
                    }
                }
            }
            return _filter!!
        }
    private var _filter: ImageVector? = null
}

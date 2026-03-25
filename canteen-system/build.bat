@echo off
echo ========================================
echo   Canteen System - Build Script
echo ========================================

cd /d %~dp0

if not exist "out" mkdir out

echo Compiling source files...
javac -d out -encoding UTF-8 ^
    src\com\canteen\entity\User.java ^
    src\com\canteen\entity\Dish.java ^
    src\com\canteen\entity\Order.java ^
    src\com\canteen\dao\OrderDAO.java ^
    src\com\canteen\service\LoginService.java ^
    src\com\canteen\service\DishService.java ^
    src\com\canteen\service\OrderService.java ^
    src\com\canteen\view\StudentView.java ^
    src\com\canteen\view\AdminView.java ^
    src\com\canteen\Main.java

if %errorlevel% equ 0 (
    echo.
    echo Build successful!
    echo.
    Starting application...
    echo.
    cd out
    java com.canteen.Main
) else (
    echo.
    echo Build failed! Please check errors.
    pause
)

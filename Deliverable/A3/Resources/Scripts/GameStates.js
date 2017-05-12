var JavaPackages = new JavaImporter( Packages.java.awt.Color, 
Packages.sage.scene.Group, 
Packages.sage.scene.shape.Pyramid,
Packages.graphicslib3D.Point3D);
with (JavaPackages)
{
   var started = false;
   var networked = false;
   var gameOver = false;
   
   var testingNetwork = true;
   var testing = false;
   var sound = false;
   
   var keyboard = "HID Keyboard Device";
   var mouse = "HID-compliant Mouse"; ;
   var gamepad = "Controller (XBOX 360 For Windows)";
}
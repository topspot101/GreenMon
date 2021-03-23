<?php
        if ($_GET['type'] == "temphum")
		$cmd = "cd /var/www/localhost/htdocs/; /var/www/localhost/htdocs/gengraph --type ".$_GET['type']." --period ".$_GET['time'];
        else if ($_GET['type'] == "ikInputOutput")
                $cmd = "cd /var/www/localhost/htdocs/; /var/www/localhost/htdocs/GenStatus --type ".$_GET['type']." --period ".$_GET['time'];
        else if ($_GET['type'] == "light")
                $cmd = "cd /var/www/localhost/htdocs/; /var/www/localhost/htdocs/GenLight --type ".$_GET['type']." --period ".$_GET['time'];
        else if ($_GET['type'] == "vpd")
                $cmd = "cd /var/www/localhost/htdocs/; /var/www/localhost/htdocs/GenKPA --type ".$_GET['type']." --period ".$_GET['time'];
        else if ($_GET['type'] == "irSurfaceTemp")
	        $cmd = "cd /var/www/localhost/htdocs/; /var/www/localhost/htdocs/GenIrTemp --type ".$_GET['type']." --period ".$_GET['time'];
	else
                $cmd = "cd /var/www/localhost/htdocs/; /var/www/localhost/htdocs/gengraph --type ".$_GET['type']." --period ".$_GET['time'];
        header('Content-type: image/png');
        passthru($cmd,$ret);


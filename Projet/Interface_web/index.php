<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <title>Remote Monitoring HMI</title>
    <link rel="stylesheet" href="style.css">
    <script type="text/javascript">
        
        var v;
        function init(){
            
            
    document.getElementById("ulist").addEventListener('change',recupererValue,false);
    console.log("init");
    
            
            
        }
        
    function recupererValue(){
            $v = document.getElementById("ulist").value; 
            document.location.href="./?user="+$v;
            console.log($v);
            
    }
</script>

    
</head>

<body onload="init();">

    <div class="left_panel">
        
     <div>  
<select id="ulist" name="userlist" form="carform">
    
    <option value="" selected>Choisir une personne</option>
    <?php
$user = $_GET['user'];
$conn = mysqli_connect('mysql.nextwab.com', 'sae7087793d21187', 'slego123','usr_sae7087793d21187');
  $sql = "SELECT DISTINCT UserId FROM ATRDB";

                $result = mysqli_query($conn, $sql);

        while (($row = mysqli_fetch_array($result, MYSQLI_ASSOC)) != NULL){
            
            $userId = $row['UserId'];
            
            echo   '<option value="'.$userId.'">'.$userId.'</option>' ;
        }

    ?>
</select>
         </div>
        <center>

            <?php

        
            $sql = "SELECT * FROM ATRDB WHERE UserId = '$user'  ORDER BY Time, Date DESC";

                $result = mysqli_query($conn, $sql);

        while (($row = mysqli_fetch_array($result, MYSQLI_ASSOC)) != NULL){
            $lastAction = $row['Action'];
            $lastheureofaction = $row['Date'];
            $lasttimeofaction =$row['Time'];
            $lasttimezoneofaction = $row['Timezone'];
        }
?>
                <div class="vignette">
                    <div class="title_of_vignette">
                        <div style="padding-left:10px;padding-top:2px;font-size:13px">Last Alert</div>


                        <img src="alarm.png" width="60px" style="padding-left:15px;float:left;margin-top:10px">
                        <div style="float:left">
                            <div style="padding-left:30px;padding-top:10px">
                                <?php echo $lastAction;?>

                            </div>
                            <div class="keyword" style="margin-left:40px">
                                <?php echo $lastheureofaction;?>
                            </div>

                            <div class="keyword">
                                <?php echo $lasttimeofaction;?>

                            </div>

                            <div class="keyword">
                                <?php echo $lasttimezoneofaction;?>
                            </div>
                        </div>



                    </div>


        </center>

        <?php
   $sql = "SELECT * FROM RAWDATA WHERE UserId = '$user' AND TypeSensor = 'Humidity' ORDER BY Time, Date  ASC  ";

           $result = mysqli_query($conn, $sql);

        while (($row = mysqli_fetch_array($result, MYSQLI_ASSOC)) != NULL){
             $UniteHumidity = $row['UnitSensor'];
            $DataHimidity = $row['DataSensor'];
            $lastDateH = $row['Date'];
            $lastTimeH = $row['Time'];
            $lastTimezoneH = $row['Timezone'];
        }



 $sql = "SELECT * FROM RAWDATA WHERE UserId = '$user' AND TypeSensor = 'Light' ORDER BY Time, Date  ASC  ";

        $result = mysqli_query($conn, $sql);

        while (($row = mysqli_fetch_array($result, MYSQLI_ASSOC)) != NULL){
            
              $UniteLight  = $row['UnitSensor'];
            $DataLight =  $row['DataSensor'];
            $lastDateL = $row['Date'];
            $lastTimeL= $row['Time'];
            $lastTimezoneL = $row['Timezone'];
            
        }


 $sql = "SELECT * FROM RAWDATA WHERE UserId = '$user' AND TypeSensor = 'PIR' ORDER BY Time, Date  ASC  ";

        $result = mysqli_query($conn, $sql);

        while (($row = mysqli_fetch_array($result, MYSQLI_ASSOC)) != NULL){
            $UnitePIR  = $row['UnitSensor'];
            $DataPIR =  $row['DataSensor'];
            $lastDateP = $row['Date'];
            $lastTimeP = $row['Time'];
            $lastTimezoneP = $row['Timezone'];
        }



?>
            <div id="circle">
                <center>
                    <img src="drop.png" width="65px" style="margin-top:20px">
                    <div style="margin-top:10px;font-size:30px">
                        <?php echo $DataHimidity . $UniteHumidity;?>
                    </div>
                    <div style="margin-top:0px;font-size:14px">
                        Humidity
                    </div>
                    <div style="margin-top:2px;font-size:9px">
                        <?php echo $lastDateH; 
                          echo '<br>';
                            echo $lastTimeH ." / ". $lastTimezoneH?>
                    </div>
                </center>
            </div>
            <div id="circle">
                <center>
                    <img src="light-bulb.png" width="65px" style="margin-top:20px">
                    <div style="margin-top:10px;font-size:30px">
                        <?php echo $DataLight . $UniteLight;?>
                    </div>
                    <div style="margin-top:0px;font-size:14px">
                        Light
                    </div>
                    <div style="margin-top:2px;font-size:9px">
                        <?php echo $lastDateL; 
                          echo '<br>';
                            echo $lastTimeL ." / ". $lastTimezoneL?>
                    </div>
                    
                </center>
            </div>
            <div id="circle">
                <center>
                    <img src="doorway.png" width="65px" style="margin-top:20px">
                    <div style="margin-top:10px;font-size:30px">
                        <?php 
                                if($DataPIR == 1){
                                echo "Somebody";
                            }else{
                            echo "Nobody";
                            }
                            ?>
                    </div>
                    <div style="margin-top:0px;font-size:14px">
                        Presence
                    </div>
                    <div style="margin-top:2px;font-size:9px">
                        <?php echo $lastDateP; 
                          echo '<br>';
                            echo $lastTimeP ." / ". $lastTimezoneP?>
                    </div>
                </center>
            </div>


            </div>
            <div class="right_panel">

                <?php
            

        
            $sql = "SELECT * FROM ATRDB WHERE UserId = '$user'  ORDER BY Time DESC, Date DESC";

                $result = mysqli_query($conn, $sql);

        while (($row = mysqli_fetch_array($result, MYSQLI_ASSOC)) != NULL){

?>
                    <div class="vignette">
                        <div class="title_of_vignette">
                            <div style="padding-left:10px;padding-top:2px;font-size:13px">
                                <?php
            if(strpos($row['Action'], 'the living')  ){
                
                     echo 'Living room'; 
            }
            
            if(strpos($row['Action'], 'the bathroom')  ){
                    echo 'Bathroom'; 
            }
            
            if(strpos($row['Action'], 'the room')  ){
                
                   echo 'Room';  
            }
            
             if(strpos($row['Action'], 'shower')  ){
                
                   echo 'Shower';  
            }
                                                                     
            ?>
                            </div>
                        </div>

                        <?php
            if(strpos($row['Action'], 'the living')  ){
                
                     echo '<img src="couch.png" width="60px" style="padding-left:15px;float:left;margin-top:10px">'; 
            }
            
            if(strpos($row['Action'], 'the bathroom')  ){
                     echo '<img src="duck.png" width="60px" style="padding-left:15px;float:left;margin-top:10px">'; 
                
            }
            
            if(strpos($row['Action'], 'the room')  ){
                
                    echo '<img src="bed.png" width="60px" style="padding-left:15px;float:left;margin-top:10px">';  
            }
            
             if(strpos($row['Action'], 'shower')  ){
                
                    echo '<img src="shower.png" width="60px" style="padding-left:15px;float:left;margin-top:10px">';  
            }
                                                                     
            ?>
                            <div style="float:left">
                                <div style="padding-left:30px;padding-top:10px">

                                    <?php echo $row['Action']; ?>
                                </div>
                                <div class="keyword" style="margin-left:40px">
                                    <?php echo $row['Date']; ?>

                                </div>

                                <div class="keyword">
                                    <?php echo $row['Time']; ?>

                                </div>

                                <div class="keyword">
                                    <?php echo $row['Timezone']; ?>

                                </div>
                            </div>
                    </div>
                    <?php  } ?>


            </div>


</body>

</html>
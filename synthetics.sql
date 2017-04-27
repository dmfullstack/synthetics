CREATE TABLE `down` (
  `created_t` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `timestamp` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `appname` varchar(100) DEFAULT NULL,
  `errorcode` varchar(200) DEFAULT NULL,
  `Issue` varchar(3) DEFAULT NULL,
  `mod_t` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=MyISAM DEFAULT CHARSET=latin1


CREATE TABLE `transactions` (
  `id` int(6) DEFAULT NULL,
  `application` varchar(100) DEFAULT NULL,
  `created_t` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `timestamp` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `elapsed` int(5) DEFAULT NULL,
  `label` varchar(100) DEFAULT NULL,
  `responsecode` varchar(100) DEFAULT NULL,
  `responsemessage` varchar(100) DEFAULT NULL,
  `threadName` varchar(100) DEFAULT NULL,
  `datatype` varchar(100) DEFAULT NULL,
  `success` varchar(100) DEFAULT NULL,
  `failuremessage` varchar(100) DEFAULT NULL,
  `bytes` int(5) DEFAULT NULL,
  `grpthreads` int(5) DEFAULT NULL,
  `allthreads` int(5) DEFAULT NULL,
  `latency` int(5) DEFAULT NULL,
  `idletime` int(5) DEFAULT NULL,
  KEY `transactionInd1` (`created_t`,`application`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1

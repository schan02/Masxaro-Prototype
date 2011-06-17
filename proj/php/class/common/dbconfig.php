<?php
/*
 * Dbconfig.php -- DB configuration 
 *
 *  Copyright 2011 World Three Technologies, Inc. 
 *  All Rights Reserved.
 * 
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Written by Yaxing Chen <Yaxing@masxaro.com>
 * 
 *  Database configuration file
 *  configure database connection data
 */


return array(
	/**
	 * AWS
	 * MariaDB
	 */
	'host' => '46.51.255.119', 
    'user' => 'w3t',
	'pwd' => 'w3t',
	'dbName' => 'w3tdb'
	
	/**
	 * localhost
	 */
//	'host' => 'localhost', 
//    'user' => 'root',
//	'pwd' => 'root',
//	'dbName' => 'w3tdb'
	
	/**
	 * Godaddy
	 * 
	 * MySQL
	 */
//	'host' => 'w3tdb.db.7762973.hostedresource.com', 
//    'user' => 'w3tdb',
//	'pwd' => 'W3TAdmin',
//	'dbName' => 'w3tdb'
);

?>
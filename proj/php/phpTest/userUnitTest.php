<?php
/*
 *  userUnitTest.php -- unit test for user control class  
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
 */

include_once '../../config.php';

class UserUnitTest extends UnitTest{
	
	public $ctrl;
	
	public function __construct(){
		parent::__construct();
		$this->ctrl = new UserCtrl();
	}
	
	public function insertUser_Test(){
		$info = array(
						'user_account'=>'utest',
						'pwd'=>'123'
					);
		$this->assertTrue($this->ctrl->insertUser($info));
	}
	
	public function fakeDeleteUser_Test($acc){
		$this->assertTrue($this->ctrl->fakeDeleteUser($acc));
	}
	
	public function recoverDeletedUser_Test($acc){
		$this->assertTrue($this->ctrl->recoverDeletedUser($acc));
	}
	
	public function realDeleteUser_Test($acc){
		$this->assertTrue($this->ctrl->realDeleteUser($acc));
	}
	
	public function chkAccount_Test($acc){
		$this->assertTrue($this->ctrl->chkAccount($acc));
	}
	
	public function updateUserInfo_Test($acc){
		$info = array(
						'user_account'=>'utest',
						'pwd'=>'123'
					);
		
		$this->assertTrue($this->ctrl->updateUserInfo($acc, $info));
	}
	
	public function findUser_Test(){
		$this->assertTrue($this->ctrl->findUser('utest', '123'));
	}
	
	public function getUserProfile_Test(){
		
	}
	
}

$test = new UserUnitTest();

$test->chkAccount_Test('utest');

$test->insertUser_Test();

$test->findUser_Test();

$test->fakeDeleteUser_Test('utest');

$test->recoverDeletedUser_Test('utest');

$test->realDeleteUser_Test('utest');

?>
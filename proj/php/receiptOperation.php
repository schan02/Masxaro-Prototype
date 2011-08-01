<?php
/*
 *  receiptOperation.php -- receipt logic 
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
 *  
 */

include_once '../config.php';
include_once 'header.php';

$opcode = $post['opcode'];
$userAcc = $post['acc'];

//following parameters are optional

/**
 * 
 * @desc
 * for mobile end
 * @var boolean $mobile
 */
$mobile = $post['mobile'];
/**
 * @desc
 * result set offset control, optional
 * 
 * @var int $limitStart
 * @var int $limitOffset
 **/
$limitStart = $post['limitStart'];
$limitOffset = $post['limitOffset'];

/**
 * @desc
 * sql options, all are optional
 * @var string $groupBy
 * @var string $orderBy
 * @var boolean $orderDesc
 */
$groupBy=$post['groupBy']; 
$orderBy=$post['orderBy'];   
$orderDesc=$post['orderDesc']; 

$ctrl = new ReceiptCtrl();

Tool::setJSON();

switch($opcode){
	case 'new_receipt_basic':
		/**
		 * POST:
		 * @param array receipt
		 * 
		 * @return false or inserted id
		 * 
		 * @example
		 *
		 * $receipt = array(
		 * 		store_account=>'Mc_NYU',
		 * 		user_account=>'w3t',
		 * 		tax=>10,
		 * );
		 * 
		 * @desc
		 * insert a receipt without items
		 */
		$basicInfo = $post['receipt'];
		echo $ctrl->insertReceipt($basicInfo, null);
		break;
	
	case 'new_item':
		/**
		 * POST:
		 * @param 2-d-array items
		 * 
		 * @return boolean or inserted id(last inserted item id)
		 * 
		 * @example
		 * $items = array(
		 * 		array(
		 * 			receipt_id=>1,
		 * 			item_id=>10,
		 * 			item_name=>'fries-mid',
		 * 			item_qty=>2,
		 * 			item_price=>1.99
		 * 		),
		 * 		array(
		 * 			receipt_id=>1,
		 * 			item_id=>2,
		 * 			item_name=>'Salad',
		 * 			item_qty=>1,
		 * 			item_price=>1
		 * 			)
		 * );
		 * 
		 * 
		 * @desc
		 * insert items for an existing receipt, need to indicate receipt id in items
		 */
		$items = $post['items'];
		echo $ctrl->insertReceipt(null, $items);
		break;
		
	case 'new_receipt':
		/**
		 * @see 'new_item'
		 * @see 'new_receipt_basic'
		 * 
		 * POST:
		 * @param array $receipt
		 * @param 2-d-array $items
		 * 
		 * @return boolean
		 * 
		 * @desc
		 * insert a complete receipt with items, don't have to indicate receipt id
		 * in items
		 * 
		 * @example
		 * $items = array(
		 * 		array(
		 * 			item_id=>10,
		 * 			item_name=>'fries-mid',
		 * 			item_qty=>2,
		 * 			item_price=>1.99
		 * 		),
		 * 		array(
		 * 			item_id=>2,
		 * 			item_name=>'Salad',
		 * 			item_qty=>1,
		 * 			item_price=>1
		 * 			)
		 * );
		 * 
		 */
		
		echo $ctrl->insertReceipt($post['receipt'], $post['items']);
		break;
		
	case 'f_delete_receipt':
		/**
		 * POST:
		 * @param $post['id'] receipt id
		 * @return boolean
		 * @desc fake delete a receipt
		 */
		echo $ctrl->fakeDelete($post['id']);
		break;
		
	case 'delete_receipt':
		/**
		 * POST:
		 * @param $post['id'] receipt id
		 * @return boolean
		 * @desc delete a receipt
		 */
		echo $ctrl->realDelete($post['id']);
		break;
		
	case 'recover':
		/**
		 * POST:
		 * @param $post['id'] receipt id
		 * @return boolean
		 * @desc recover a fake deleted receipt
		 */
		echo $ctrl->recoverDeleted($post['id']);
		break;
		
	case 'user_get_all_receipt':
		/**
		 * POST:
		 * @param $userAcc
		 * @return json
		 * @desc get all user receipts, including items
		 * 
		 * @example
		 * return:
		 * [{"id":"3","store_name":"McDonalds(NYU)","user_account":null,
		 *   "receipt_time":"07-17-2011 12:32 PM","tax":"0.09","total_cost":"99.00",
		 *   "currency_mark":"$","source":"default","img":null,"deleted":0,
		 *   "items":[{"item_id":"3","item_name":"Harry-Potter - IIIII123123123123",
		 *             "item_qty":"1","item_discount":"1.00","item_price":"10.99"},
		 *            {"item_id":"4","item_name":"Harry-potter - II",
		 *             "item_qty":"2","item_discount":"1.00","item_price":"39.99"},
		 *            {"item_id":"5","item_name":"Harry-potter - III",
		 *             "item_qty":"5","item_discount":"1.00","item_price":"19.99"}],
		 *   "tags":["book","food"]}]
		 */
		$con = array(
				'='=>array(
						'field'=>'receipt.user_account',
						'value'=>$userAcc
					)
		);
		echo json_encode(
					$ctrl->searchReceipt($con,$userAcc, $limitStart, $limitOffset, 
											  	$groupBy, $orderBy, $orderDesc)
			);
		break;
		
	case 'user_get_receipt_items':
		/**
		 * POST:
		 * @param int $post['receiptId']
		 * 
		 * @see user_get_all_receipt example-items
		 */
		echo json_encode($ctrl->userGetReceiptItems($post['receiptId']));
		break;
	
	case 'user_get_receipt_detail':
		/**
		 * POST:
		 * @param int $post['receiptId']
		 * 
		 * @see user_get_all_receipt example-basic
		 */
		echo json_encode($ctrl->getReceiptDetail($post['receiptId']));
		break;
	
	case 'search':
		/**
		 * @see searchingConHandler()
		 * 
		 * @return @see user_get_all_receipt 
		 * 
		 * POST (optional):
		 * @param array keys
		 * 
		 * @param array $tags
		 * 
		 * @param array $timeRange date format: YYYY-MM-DD HH:MM:SS or YYYY-MM-DD 
		 * 
		 * @example    
		 * 'timeRange'=>array('start'=>'2010-01-01', 'end'=>'2011-12-01'), 
		 * 
		 * 
		 */
		$timeStart = $post['timeRange']['start'];
		$timeEnd = $post['timeRange']['end'];
		
		$keys = isset($post['keys']) ? $post['keys'] : '';
		$tags = $post['tags'];
		
		$con = searchingConHandler($keys, $tags, $timeStart, $timeEnd);
		echo json_encode(
							$ctrl->searchReceipt($con,$userAcc, $limitStart, $limitOffset, 
											  	$groupBy, $orderBy, $orderDesc, $mobile)
						);
		break;
	
	case 'get_store_receipts':
		/**
		 * 
		 * @desc get receipts from a certain store
		 * @param $post['store']
		 */
		$store = $post['store'];
		$con = array(
				'='=>array(
						'field'=>'store_name',
						'value'=>$store
					)
		);
		echo json_encode(
							$ctrl->searchReceipt($con,$userAcc, $limitStart, $limitOffset, 
											  	$groupBy, $orderBy, $orderDesc)
						);
		break;
		
	case 'get_source_receipts':
		//get receipts from certain sources
		//sources: array('email', 'mobile');
		
		$sources = $post['sources'];
		
		$orConds = array();
		$i = 0;
		foreach($sources as $source){
			$orConds['=:'.$i++] = array(
													'field'=>'source',
													'value'=>$source
												);
		}
		
		$con = array(
					'OR'=>$orConds,
		);
		
		echo json_encode(
							$ctrl->searchReceipt($con,$userAcc, $limitStart, $limitOffset, 
											  	$groupBy, $orderBy, $orderDesc)
						);
		break;
		
	default:
		die('wrong parameter');
}

/**
 * @desc
 * search receipts based on keywords in item_name and store_name,
 * and time range, tags
 * 
 * multiple keywords should be organized as an 1-d array
 * keys=>array(key1, key2, key3...), eg. keys=>array('Coffee', 'coke')
 * 
 * multiple tags should be organized as an 1-d array
 * tags=>array(tag1, tag2, tag3...), eg. tags=>array('food', 'restaurant')
 * 
 * time range should be organized as an 1-d array (at least one of start, end should be set)
 * array('timeRange'=>array('start'=>'', 'end'=>''))
 * 
 * POST (optional):
 * @param array keys
 * 
 * @param array $tags
 * 
 * @param array $timeRange 'timeRange'=>array('start'=>'', 'end'=>''), 
 *                          date format: YY-MM-DD HH:MM:SS or YY-MM-DD
 * 
 * @return
 * encoded JSON of receipt object array
 **/
function searchingConHandler($keys, $tags, $timeStart, $timeEnd){
	
	$con = array();
	
		//keys
	if(!is_array($keys)){
		$keys = "%$keys%";
		
		// 'item_name LIKE %keys% OR store_name LIKE %$keys%'
		$con['OR'] = array(
				'LIKE:0'=>array(
						'field'=>'item_name',
						'value'=>$keys
					),
				'LIKE:1'=>array(
					'field'=>'store_name',
					'value'=>$keys
				)
			);
	}
	else{
		$tmp = array();
		$i = 0;
		foreach($keys as $key){
			$key = "%$key%";
			$tmp['like:'.$i ++] = array(
								'field'=>'item_name',
								'value'=>$key
				);
				$tmp['like:'.$i ++] = array(
									'field'=>'store_name',
									'value'=>$key
				);
			}
		$con['OR'] = $tmp;
	}
	
	//tags
	if(isset($tags) && is_array($tags)){
		$tmp = array();
		$i = 0;
		foreach($tags as $tag){
			$tag = "%$tag%";
			$tmp['like:'.$i ++] = array(
								'field'=>'tag',
								'value'=>$tag
			);
	}
	$con['OR:'."1"] = $tmp;
	$con['='] = array(
			'field'=>'`receipt_tag`.`receipt_id`',
			'field:1'=>'`receipt`.`id`'
		);
	}
	
	//time range
	if(isset($timeStart)){
		$con['>='] = array(
			'field'=>'receipt_time',
			'value'=>$timeStart
		);
	}
	if(isset($timeEnd)){
		$con['<='] = array(
			'field'=>'receipt_time',
			'value'=>$timeEnd
		);
	}
	
	$con['value'] = 1;
	
	//organize into AND condition
	$tmp = array();
	$tmp['AND'] = $con;
	$con = $tmp;
	
	return $con;
}

?>

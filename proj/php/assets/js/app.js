/*
  Copyright 2011 World Three Technologies, Inc. 
  All Rights Reserved.

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
  */
var Receipt = Backbone.Model.extend({

  url: 'receiptOperation.php',

  initialize:function(){
    _.bindAll(this,'sync');
  },

  sync:function(method,model,options){
    model.set({"user_account":account});
    var data;
    if(method == "read"){
      data = {
        opcode : "user_get_receipt_detail",
        receipt_id: model.get("receipt_id"),
      }
    }else if(method == "delete"){
      data = {
        opcode : "f_delete_receipt",
        receipt_id: model.get("receipt_id")
      }
    }
    $.post(this.url,data,options.success).error(options.error);
  }
});
var Receipts = Backbone.Collection.extend({
  model: Receipt,

  url: 'receiptOperation.php',

  initialize:function(){
    _.bindAll(this,"sync","search","searchTag");
  },

  sync:function(method,model,options){
    var data;
    if(method == "read"){
      data = {
        opcode : "user_get_all_receipt",
        acc: this.account
      }
    }
    $.post(this.url,data,options.success).error(options.error);
  },

  search:function(query,success){
    var model = this;
    $.post(this.url,{
      opcode : "key_search",
      acc: account,
      key : query
    }).success(function(data){
      model.reset(data);
      success();
    });
  },

  searchTag:function(tags,success){
    var model = this;
    $.post(this.url,{
      opcode : "tag_search",
      acc: account,
      tags : tags,
    }).success(function(data){
      model.reset(data);
      success();
    });
  }
});
var User = Backbone.Model;
window.ActionView = Backbone.View.extend({
  
  el:$("#action-bar"),

  initialize:function(){
    _.bindAll(this,"setTags","setActive","setActiveByTag");   
  },

  isSet:false,

  events:{
    "click .action li":"setActive"
  },

  setTags:function(tag){
    if(this.isSet){
      this.setActiveByTag(tag);
      return;
    }
    var view = this;
    $.post("tagOperation.php",{
      opcode : "get_user_tags",
      user_account: account,
    }).success(function(data){
      var tags = JSON.parse(data);
      _.each(tags,function(tag){
        this.$(".action").append("<li class='tag-"+ tag +
                                 "'><a href='#tag/"+tag+"'>"+ tag +"</a></li>");
      });
      view.setActiveByTag(tag);
      view.isSet = true;
    });
  },

  setActive:function(){
    this.$(".active").removeClass("active");
    this.$(event.target).parent().addClass("active");
  },

  setActiveByTag:function(tag){
    this.$(".active").removeClass("active");
    this.$(".tag-"+tag).addClass("active");
  }
});
window.AppView = Backbone.View.extend({
  el:$("#receipts"),

  pageSize:10,

  start:1,

  end:1,

  initialize:function(){
    _.bindAll(this,"render","renderMore","renderReceipt","cleanResults",
                  "setEnd","search","category","after");
    //hack:: should move the logic of before together
    this.model.bind("sync",this.before);
    this.model.bind("reset",this.render);
    this.model.bind("change",this.render);
  },

  events:{
    "click .more": "renderMore",
    "click #search-button": "searchByForm",
    "keyup #search-query": "submitSearch"
  },

  search:function(query){
    this.before();
    this.model.search(query,this.after);
  },

  submitSearch:function(event){
    if(event.which == 13){
      this.searchByForm();
    }
  },

  before:function(){
    $('#ajax-loader').show();
    $('.receipts-stat').hide();
    this.$('.row').remove();
  },

  after:function(){
    $('#ajax-loader').hide();
    $('.receipts-stat').show();
  },

  searchByForm:function(){
    this.search($('#search-query').val());
  },

  category:function(category){
    this.before();
    this.model.category(category,this.after);       
  },

  updateStatus:function(){
    this.$(".receipts-stat .stat").text(this.start + " to "+ this.end +" in "+this.model.length);
  },

  render:function(){
    this.cleanResults();
    this.setEnd();

    this.$('#ajax-loader').hide();

    _.each(this.model.models.slice(0,this.end),this.renderReceipt);
    this.updateStatus();

    if(this.end >= this.model.length ){
      this.$(".more").hide();
    }
    return this;
  },

  cleanResults:function(){
    this.$('.row').remove();
  },

  renderMore:function(){
    var pageLength = (this.end + this.pageSize <= this.model.length) 
                     ? this.end + this.pageSize : this.model.length;
    _.each(this.model.models.slice(this.end,pageLength),this.renderReceipt);

    this.end = pageLength;
    this.updateStatus();

    if(this.end === this.model.length){
      this.$(".more").hide();
    }
  },

  renderReceipt:function(receipt){
    var view = new ReceiptView({model:receipt});
    this.el.children("table").append(view.render().el);
  },

  setEnd:function(){
    this.end = (this.model.length > 10) ? 10 : this.model.length;       
  },

  searchTag:function(tags){
    this.before();
    this.model.searchTag(tags.split("-"),this.after);
  }
});
var ReceiptView = Backbone.View.extend({

  tagName:"tr",
  className:"row",
  template:_.template($('#receipt-row-template').html() || "<div/>"),
  fullTemplate:_.template($('#receipt-full-template').html() || "<div/>"),
  itemTemplate:_.template($('#receipt-item-template').html() || "<div/>"),

  initialize:function(){
    _.bindAll(this,'render','showReceipt','getItemText','edit','afterEdit');
    this.model.bind('change',this.render);
  },

  events:{
    "click .receipt-row" : "showReceipt",
    "click .close" :"render",
  },

  render:function(){
    var view = $(this.el);
    view.html(this.template(this.model.toJSON()));

    var text = this.getItemText(this.model.get("items"));
    view.find(".items").html(text);
    var tags = this.getTags(this.model.get("tags"));
    view.find(".tags").html(tags);
    view.find(".date").html(new Date(this.model.get("receipt_time")).format());
    return this;
  },

  edit:function(event){
    var receipt = $(event.target).parent().parent();
    receipt.addClass("editing");
  },

  afterEdit:function(event){
    var receipt = $(event.target).parent().parent(),
        name = receipt.find("input.item_name").val(),
        category = receipt.find("input.item_category").val()
    receipt.removeClass("editing");
    receipt.find("span.item_name").text(name);
    receipt.find("a.item_category").text(category);
  },

  showReceipt:function(){

    if(window.lastOpen){
      window.lastOpen.render();
    }

    $(this.el).html(this.fullTemplate(this.model.toJSON()));
    $(this.el).find(".date").html(new Date(this.model.get("receipt_time")).format());

    var items = $(this.el).find(".items"),
        self = this;

    _.each(self.model.get("items"),function(model){
      items.append(self.itemTemplate(model));
    });

    var tags = this.getLinkTags(this.model.get("tags"));
    $(this.el).find(".tags").html(tags);

    window.lastOpen = this;
  },

  getItemText:function(items){
    return _.reduce(items,function(memo,item){
      return memo + item.item_name + ", ";
    },"").slice(0,-2);
  },

  getTags:function(tags){
    return _.reduce(tags,function(html,tag){
      return html + "<span class='tag'>" + tag + "</span>";
    },"");        
  },

  getLinkTags:function(tags){
    return _.reduce(tags,function(html,tag){
      return html + "<span class='tag'><a href='index.php#tag/"+tag+"'>"
                  + tag + "</a></span>";
    },"");        
  }
});
var UserView = Backbone.View.extend({

  initialize:function(){
    _.bindAll(this,"render");
    this.el = $("#user");
    this.render();
  },

  render:function(){
    $("#username").text(this.model.get("account")); 
    return this;
  }
});
var AppRouter = Backbone.Router.extend({

  initialize: function(){
    _.bindAll(this,"index","search","searchTag");
    var user = this.user = new User({
      account:readCookie("user_acc"),
    });

    var receipts = this.receipts = new Receipts();
    window.account = receipts.account = user.get("account");
    window.appView = new AppView({model:receipts });
    window.userView = new UserView({model:user});
    window.actionView = new ActionView();
  },

  routes: {
    "" : "index",
    "index" : "index",      
    "search/:query" : "search",
    "tag/:tag" : "searchTag"
  },

  index: function(){
    var options = {
      error: function(){
        $("#ajax-loader").html("<h3>error in model request</h3>");
      }
    }
    this.receipts.fetch(options);
    actionView.setTags("recent");
  },

  search: function(query){
    appView.search(query);      
  },

  searchTag: function(tag){
    actionView.setTags(tag);
    appView.searchTag(tag);           
  }
});
$(function(){
  new AppRouter();
  Backbone.history.start({pushState:false});
});

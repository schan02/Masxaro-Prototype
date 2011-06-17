window.AppView = Backbone.View.extend({
  el:$("#receipts"),

  pageSize:10,

  start:1,

  end:1,

  initialize:function(){
    _.bindAll(this,"render","renderMore","renderReceipt","setEnd");
    this.model.bind("refresh",this.render);
    this.model.bind("change",this.render);
    this.model.view = this;
  },

  events:{
    "click .more": "renderMore",
  },

  updateStatus:function(){
    this.$(".receipts-stat .stat").text(this.start + " to "+ this.end +" in "+this.model.length);
  },

  render:function(){
    this.setEnd();
    _.each(this.model.models.slice(0,this.end),this.renderReceipt);
    this.updateStatus();
    if(this.end >= this.model.length ){
      this.$(".more").hide();
    }
  },

  renderMore:function(){
    var pageLength = (this.end + this.pageSize <= this.model.length) ? this.end + this.pageSize : this.model.length;
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
  }
});
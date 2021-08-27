<template>

  <transition name="modal">
    <div class="es-modal-mask">
      <div class="es-modal-wrapper">
        <div class="es-modal-container es-box" :style="modalContainerStyle">

          <div class="es-modal-header es-row header">
            <slot name="header"/>
          </div>

          <div class="es-modal-body es-row content">
            <slot name="body"/>
          </div>

          <div class="es-modal-buttons es-row footer">
            <slot name="buttons">
              <button class="es-modal-default-button" @click="$emit('close')">
                OK
              </button>
            </slot>
          </div>

        </div>
      </div>
    </div>
  </transition>

</template>

<script>
export default {
  name: "Modal",
  props: {
    width: {
      type: String,
      default: "300px"
    },
    height:{
      type: String,
      default: "auto"
    }
  },
  computed:{
    modalContainerStyle(){
      return `width:${this.width};height:${this.height};`
    }
  }
}
</script>

<style>
.es-modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.2);
  display: table;
  transition: opacity 5.3s ease;
}

.es-modal-wrapper {
  display: table-cell;
  vertical-align: middle;
}

.es-modal-container {
  /*width: 300px;*/

  margin: 0px auto;
  padding: 10px 20px;
  background-color: #fff;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
  transition: all 0.3s ease;
  font-family: Helvetica, Arial, sans-serif;
}

/*.es-modal-header h3 {
  margin-top: 0;
  color: #42b983;
}*/

.es-modal-body {
  margin: 10px 0;
}

/*.es-modal-default-button {*/
/*  display: block;*/
/*  margin-top: 1rem;*/
/*}*/
.es-modal-buttons{
  margin-top: 1rem;
}

/*
 * The following styles are auto-applied to elements with
 * transition="modal" when their visibility is toggled
 * by Vue.js.
 *
 * You can easily play with the modal transition by editing
 * these styles.
 */

.modal-enter {
  opacity: 0;
}

.modal-leave-active {
  opacity: 0;
}

.modal-enter .modal-container,
.modal-leave-active .modal-container {
  -webkit-transform: scale(1.1);
  transform: scale(1.1);
}
</style>
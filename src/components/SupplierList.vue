<template>
  <div class="container">
    <h1>Toptancı Listesi</h1>
    <button @click="showForm = !showForm" class="add-button">
      Yeni Toptancı Ekle
    </button>
    <div v-if="showForm" class="add-supplier-form">
      <h2>Yeni Toptancı Ekle</h2>
      <form @submit.prevent="addSupplier">
        <label for="name">Toptancı Adı:</label>
        <input type="text" v-model="newSupplier.name" id="name" required>
        <button type="submit">Ekle</button>
      </form>
    </div>
    <ul class="supplier-list">
      <li v-for="supplier in suppliers" :key="supplier.id" class="supplier-item">
        <router-link :to="'/supplier/' + supplier.id" class="supplier-link">
          {{ supplier.name }}
        </router-link> 
        <span class="info">- Toplam Fatura: <strong>{{ getTotalInvoices(supplier) }}</strong></span> 
        <span class="info">- Yapılan Ödeme: <strong>{{ getTotalPayments(supplier) }}</strong></span> 
        <span class="info">- Borç: <strong>{{ getDebt(supplier) }}</strong></span>
      </li>
    </ul>
    <!-- Toplam Borç Gösterimi -->
    <div class="total-debt">
      <h2>Toplam Borç: {{ getTotalDebt() }} TL</h2>
    </div>
  </div>
</template>
  
<script>
import axios from 'axios';

export default {
  data() {
    return {
      suppliers: [],
      showForm: false,
      newSupplier: {
        name: ''
      }
    };
  },
  created() {
    this.fetchSuppliers();
  },
  methods: {
    async fetchSuppliers() {
      try {
        const response = await axios.get('http://localhost:8080/api/suppliers/all');
        this.suppliers = response.data.sort((a, b) => a.name.localeCompare(b.name));
      } catch (error) {
        console.error("Veriler alınırken bir hata oluştu: ", error);
      }
    },
    async addSupplier() {
      try {
        await axios.post('http://localhost:8080/api/suppliers', this.newSupplier);
        this.showForm = false;
        this.newSupplier.name = '';
        this.fetchSuppliers(); // Yeni toptancıyı listeye eklemek için verileri yeniden al
      } catch (error) {
        console.error("Yeni toptancı eklenirken bir hata oluştu: ", error);
      }
    },
    getTotalInvoices(supplier) {
      return supplier.invoices.reduce((total, invoice) => total + invoice.amount, 0);
    },
    getTotalPayments(supplier) {
      return supplier.invoices.reduce((total, invoice) => {
        return total + invoice.payments.reduce((paymentTotal, payment) => paymentTotal + payment.amount, 0);
      }, 0);
    },
    getDebt(supplier) {
      const totalInvoices = this.getTotalInvoices(supplier);
      const totalPayments = this.getTotalPayments(supplier);
      return totalInvoices - totalPayments;
    },
    getTotalDebt() {
      return this.suppliers.reduce((totalDebt, supplier) => {
        return totalDebt + this.getDebt(supplier);
      }, 0);
    }
  }
};
</script>
  
  <style>
  .container {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
  }
  
  h1 {
    text-align: center;
    color: #333;
  }
  
  .add-button {
    display: block;
    margin: 20px auto;
    padding: 10px 20px;
    font-size: 16px;
    color: #fff;
    background-color: #42b983;
    border: none;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .add-button:hover {
    background-color: #369d77;
  }
  
  .add-supplier-form {
    margin-bottom: 20px;
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .add-supplier-form h2 {
    margin-top: 0;
  }
  
  .add-supplier-form label {
    display: block;
    margin-bottom: 10px;
  }
  
  .add-supplier-form input {
    width: 100%;
    padding: 8px;
    margin-bottom: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
  
  .add-supplier-form button {
    display: block;
    width: 100%;
    padding: 10px;
    font-size: 16px;
    color: #fff;
    background-color: #42b983;
    border: none;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .add-supplier-form button:hover {
    background-color: #369d77;
  }
  
  .supplier-list {
    list-style-type: none;
    padding: 0;
  }
  
  .supplier-item {
    background-color: #f9f9f9;
    padding: 15px;
    margin: 10px 0;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .supplier-link {
    color: #42b983;
    text-decoration: none;
    font-weight: bold;
    font-size: 20px;
  }
  
  .supplier-link:hover {
    text-decoration: underline;
  }
  
  .info {
    display: block;
    margin-top: 5px;
    color: #555;
  }
  .total-debt {
  margin-top: 20px;
  text-align: right;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}
  </style>
  
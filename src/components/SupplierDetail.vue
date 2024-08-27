<template>
  <div class="container">
    <h1>Toptancı Detayı</h1>
    <div v-if="supplier">
      <h2>{{ supplier.name }}</h2>

      <!-- Ödenmeyen Faturalar Bölümü -->
      <div class="invoice-section">
        <h3>Ödenmeyen Faturalar</h3>
        <button class="add-invoice-button" @click="openAddInvoiceModal">Fatura Ekle</button>
        <div class="invoice-list">
          <div v-for="invoice in unpaidInvoices" :key="invoice.id" class="invoice-item">
            <div class="invoice-info">
              <p><strong>Tarih:</strong> {{ invoice.date }}</p>
              <p><strong>Tutar:</strong> {{ invoice.amount }}₺</p>
              <p><strong>Açıklama:</strong> {{ invoice.description }}</p>
            </div>
            <div class="actions">
              <button @click="openEditInvoice(invoice)">Düzenle</button>
              <button @click="openPaymentModal(invoice)">Fatura Öde</button>
              <button @click="confirmDeleteInvoice(invoice.id)">Fatura Sil</button>
            </div>
          </div>
        </div>
        <div class="pagination">
          <button @click="prevPage('unpaid')" :disabled="unpaidInvoicesPage === 1">Önceki</button>
          <button @click="nextPage('unpaid')" :disabled="unpaidInvoicesPage >= totalUnpaidPages">Sonraki</button>
        </div>
      </div>

      <!-- Ödenmiş Faturalar Bölümü -->
      <div class="invoice-section">
        <h3>Ödenen Faturalar</h3>
        <div class="invoice-list">
          <div v-for="invoice in paidInvoices" :key="invoice.id" class="invoice-item">
            <div class="invoice-info">
              <p><strong>Tarih:</strong> {{ invoice.date }}</p>
              <p><strong>Tutar:</strong> {{ invoice.amount }}₺</p>
              <p><strong>Açıklama:</strong> {{ invoice.description }}</p>
            </div>
            <div v-if="invoice.payments.length > 0" class="payment-info">
              <p><strong>Ödeme Tarihi:</strong> {{ invoice.payments[0].paymentDate }}</p>
              <p><strong>Ödeme Yöntemi:</strong> {{ formatPaymentMethod(invoice.payments[0].paymentMethod) }}</p>
            </div>
            <div class="actions">
              <button @click="openEditInvoice(invoice)">Düzenle</button>
              <button @click="confirmDeleteInvoice(invoice.id)">Fatura Sil</button>
            </div>
          </div>
        </div>
        <div class="pagination">
          <button @click="prevPage('paid')" :disabled="paidInvoicesPage === 1">Önceki</button>
          <button @click="nextPage('paid')" :disabled="paidInvoicesPage >= totalPaidPages">Sonraki</button>
        </div>
      </div>
    </div>
    <div v-else>
      <p>Yükleniyor...</p>
    </div>

    <!-- Düzenleme Modalı -->
    <div v-if="editInvoiceModal" class="modal">
      <div class="modal-content">
        <h2>Fatura Düzenle</h2>
        <form @submit.prevent="updateInvoice">
          <label for="date">Tarih:</label>
          <input type="date" v-model="selectedInvoice.date" id="date" />

          <label for="amount">Tutar:</label>
          <input type="number" v-model="selectedInvoice.amount" id="amount" />

          <label for="description">Açıklama:</label>
          <input type="text" v-model="selectedInvoice.description" id="description" />

          <label for="paymentStatus">Ödeme Durumu:</label>
          <select v-model="selectedInvoice.paymentStatus" id="paymentStatus">
            <option :value="true">Ödendi</option>
            <option :value="false">Ödenmedi</option>
          </select>

          <button type="submit">Güncelle</button>
          <button @click="closeEditInvoice" class="cancel-button">İptal</button>
        </form>
      </div>
    </div>

    <!-- Fatura Ödeme Modalı -->
    <div v-if="paymentModal" class="modal">
      <div class="modal-content">
        <h2>Fatura Öde</h2>
        <form @submit.prevent="submitPayment">
          <label for="amount">Tutar:</label>
          <input type="number" v-model="paymentAmount" id="amount" />

          <label for="paymentDate">Ödeme Tarihi:</label>
          <input type="date" v-model="paymentDate" id="paymentDate" />

          <label for="paymentMethod">Ödeme Yöntemi:</label>
          <select v-model="paymentMethod" id="paymentMethod">
            <option value="CASH">Nakit</option>
            <option value="CREDIT_CARD">Kredi kartı</option>
          </select>

          <button type="submit">Öde</button>
          <button @click="closePaymentModal" class="cancel-button">İptal</button>
        </form>
      </div>
    </div>

    <!-- Fatura Ekle Modalı -->
    <div v-if="addInvoiceModal" class="modal">
      <div class="modal-content">
        <h2>Fatura Ekle</h2>
        <form @submit.prevent="submitNewInvoice">
          <label for="newDate">Tarih:</label>
          <input type="date" v-model="newInvoice.date" id="newDate" />

          <label for="newAmount">Tutar:</label>
          <input type="number" v-model="newInvoice.amount" id="newAmount" />

          <label for="newDescription">Açıklama:</label>
          <input type="text" v-model="newInvoice.description" id="newDescription" />

          <label for="newPaymentStatus">Ödeme Durumu:</label>
          <select v-model="newInvoice.paymentStatus" id="newPaymentStatus">
            <option :value="true">Ödendi</option>
            <option :value="false">Ödenmedi</option>
          </select>

          <button type="submit">Ekle</button>
          <button @click="closeAddInvoiceModal" class="cancel-button">İptal</button>
        </form>
      </div>
    </div>

    <!-- Silme Onay Modalı -->
    <div v-if="confirmDeleteModal" class="modal">
      <div class="modal-content">
        <h2>Fatura Sil</h2>
        <p>Faturayı silmek istediğinize emin misiniz?</p>
        <button @click="deleteInvoice">Evet</button>
        <button @click="cancelDelete" class="cancel-button">Hayır</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      supplier: null,
      unpaidInvoices: [],
      paidInvoices: [],
      unpaidInvoicesPage: 1,
      paidInvoicesPage: 1,
      invoicesPerPage: 10,
      totalUnpaidPages: 0,
      totalPaidPages: 0,
      paymentModal: false,
      paymentAmount: null,
      paymentDate: new Date().toISOString().slice(0, 10),
      paymentMethod: "CASH",
      selectedInvoice: null,
      editInvoiceModal: false,
      addInvoiceModal: false,
      newInvoice: {
        date: new Date().toISOString().slice(0, 10),
        amount: null,
        description: "",
        paymentStatus: false,
      },
      deleteInvoiceId: null,
      confirmDeleteModal: false,
    };
  },
  created() {
    this.fetchSupplier();
  },
  methods: {
    async fetchSupplier() {
      try {
        const supplierResponse = await axios.get(`http://localhost:8080/api/suppliers/${this.$route.params.id}`);
        this.supplier = supplierResponse.data;
        await this.fetchInvoices();
      } catch (error) {
        console.error("Toptancı detayları alınırken bir hata oluştu: ", error);
      }
    },
    async fetchInvoices() {
  try {
    const response = await axios.get(`http://localhost:8080/api/invoices/bySupplierId/${this.$route.params.id}`);
    let invoices = response.data;

    // Faturaları ID'ye göre azalan sırayla sırala (büyükten küçüğe)
    invoices = invoices.sort((a, b) => b.id - a.id);

    // Ödenmeyen ve ödenen faturaları ayır
    const unpaidInvoices = invoices.filter((invoice) => !invoice.paymentStatus);
    const paidInvoices = invoices.filter((invoice) => invoice.paymentStatus);

    // Ödenmeyen faturalar için sayfalamayı hesapla
    this.totalUnpaidPages = Math.ceil(unpaidInvoices.length / this.invoicesPerPage);
    const unpaidStartIndex = (this.unpaidInvoicesPage - 1) * this.invoicesPerPage;
    this.unpaidInvoices = unpaidInvoices.slice(unpaidStartIndex, unpaidStartIndex + this.invoicesPerPage);

    // Ödenmiş faturalar için sayfalamayı hesapla
    this.totalPaidPages = Math.ceil(paidInvoices.length / this.invoicesPerPage);
    const paidStartIndex = (this.paidInvoicesPage - 1) * this.invoicesPerPage;
    this.paidInvoices = paidInvoices.slice(paidStartIndex, paidStartIndex + this.invoicesPerPage);
  } catch (error) {
    console.error("Faturalar alınırken bir hata oluştu: ", error);
  }
},

    async submitNewInvoice() {
  try {
    // supplierId'yi mevcut supplier'dan al
    const supplierId = this.$route.params.id;

    // Yeni fatura nesnesine supplierId'yi ekle
    const invoiceData = {
      ...this.newInvoice,
      supplierId: supplierId,
    };

    // POST isteğini supplierId ile birlikte gönder
    await axios.post(`http://localhost:8080/api/invoices`, invoiceData);

    // Yeni fatura formunu sıfırla
    this.newInvoice = {
      date: new Date().toISOString().slice(0, 10),
      amount: null,
      description: "",
      paymentStatus: false,
    };

    // Modalı kapat ve faturaları tekrar yükle
    this.addInvoiceModal = false;
    await this.fetchInvoices();
  } catch (error) {
    console.error("Yeni fatura eklenirken bir hata oluştu: ", error);
      }
    },
    async submitPayment() {
  try {
    const paymentData = {
      amount: this.paymentAmount,
      paymentDate: this.paymentDate,
      paymentMethod: this.paymentMethod,
      invoiceId: this.selectedInvoice.id, // Include the invoiceId in the payload
    };
    // Send the POST request to the correct endpoint
    await axios.post('http://localhost:8080/api/payments', paymentData);
    this.paymentModal = false;
    await this.fetchInvoices();
  } catch (error) {
    console.error("Ödeme yapılırken bir hata oluştu: ", error);
  }
},
    async updateInvoice() {
      try {
        await axios.put(`http://localhost:8080/api/invoices/${this.selectedInvoice.id}`, this.selectedInvoice);
        this.editInvoiceModal = false;
        await this.fetchInvoices();
      } catch (error) {
        console.error("Fatura güncellenirken bir hata oluştu: ", error);
      }
    },
    async deleteInvoice() {
      try {
        await axios.delete(`http://localhost:8080/api/invoices/${this.deleteInvoiceId}`);
        this.confirmDeleteModal = false;
        this.deleteInvoiceId = null;
        await this.fetchInvoices();
      } catch (error) {
        console.error("Fatura silinirken bir hata oluştu: ", error);
      }
    },
    cancelDelete() {
      this.confirmDeleteModal = false;
      this.deleteInvoiceId = null;
    },
    openEditInvoice(invoice) {
      this.selectedInvoice = { ...invoice };
      this.editInvoiceModal = true;
    },
    closeEditInvoice() {
      this.editInvoiceModal = false;
      this.selectedInvoice = null;
    },
    openPaymentModal(invoice) {
      this.selectedInvoice = invoice;
      this.paymentAmount = null;
      this.paymentDate = new Date().toISOString().slice(0, 10);
      this.paymentMethod = "CASH";
      this.paymentModal = true;
    },
    closePaymentModal() {
      this.paymentModal = false;
      this.selectedInvoice = null;
    },
    openAddInvoiceModal() {
      this.addInvoiceModal = true;
    },
    closeAddInvoiceModal() {
      this.addInvoiceModal = false;
    },
    confirmDeleteInvoice(invoiceId) {
      this.deleteInvoiceId = invoiceId;
      this.confirmDeleteModal = true;
    },
    prevPage(type) {
      if (type === 'unpaid' && this.unpaidInvoicesPage > 1) {
        this.unpaidInvoicesPage--;
        this.fetchInvoices();
      } else if (type === 'paid' && this.paidInvoicesPage > 1) {
        this.paidInvoicesPage--;
        this.fetchInvoices();
      }
    },
    nextPage(type) {
      if (type === 'unpaid' && this.unpaidInvoicesPage < this.totalUnpaidPages) {
        this.unpaidInvoicesPage++;
        this.fetchInvoices();
      } else if (type === 'paid' && this.paidInvoicesPage < this.totalPaidPages) {
        this.paidInvoicesPage++;
        this.fetchInvoices();
      }
    },
    formatPaymentMethod(method) {
      return method === "CASH" ? "Nakit" : "Kredi kartı";
    }
  }
};
</script>

<style scoped>
.container {
  font-family: Arial, sans-serif;
  padding: 20px;
}

.invoice-section {
  margin-bottom: 40px;
}

.invoice-list {
  margin-top: 20px;
}

.invoice-item {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.invoice-info p {
  margin: 5px 0;
}

.actions {
  margin-top: 10px;
}

button {
  margin-right: 10px;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1em;
  transition: background-color 0.3s ease;
}

button:hover {
  opacity: 0.9;
}

.add-invoice-button {
  background-color: #007bff;
  color: white;
}

.add-invoice-button:hover {
  background-color: #0056b3;
}

.cancel-button {
  background-color: #dc3545;
  color: white;
}

.cancel-button:hover {
  background-color: #c82333;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  text-align: center;
  animation: fadeIn 0.3s ease;
}

.modal-content h2 {
  font-size: 1.8em;
  margin-bottom: 20px;
}

.modal-content p {
  margin-bottom: 20px;
  color: #555;
}

.modal-content form {
  display: flex;
  flex-direction: column;
}

.modal-content label {
  font-size: 1em;
  margin-bottom: 5px;
  text-align: left;
}

.modal-content input,
.modal-content select {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-bottom: 15px;
  font-size: 1em;
}

.modal-content button {
  padding: 12px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1em;
  transition: background-color 0.3s ease;
}

.modal-content button:first-of-type {
  background-color: #007bff;
  color: white;
  border: none;
  margin-right: 10px;
}

.modal-content button:first-of-type:hover {
  background-color: #0056b3;
}

.modal-content button:last-of-type {
  background-color: #dc3545;
  color: white;
  border: none;
}

.modal-content button:last-of-type:hover {
  background-color: #c82333;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>

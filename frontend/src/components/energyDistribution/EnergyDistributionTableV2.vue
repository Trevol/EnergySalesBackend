<template>
  <table class="ed-table table table-bordered table-hover table-sm">

    <thead>
    <tr>
      <th scope="col" class="fit-content">Организация</th>
      <th scope="col" class="fit-content">Пред. показ.</th>
      <th scope="col" class="fit-content">Наст. показ.</th>
      <th scope="col">Разница</th>
      <th scope="col">Расход</th>
      <th scope="col">НПМ(квт)</th>
      <th scope="col" class="fit-content">K</th>
      <th scope="col" class="fit-content">№сч-ка</th>
      <th scope="col">Примечание</th>
    </tr>
    </thead>

    <tbody>

    <template v-for="topUnit in toplevelUnits" :key="topUnit.name+topUnit.id">
      <tr class="parent-organization">
        <td class="fit-content">{{ topUnit.name }}</td>
        <td></td>
        <td></td>
        <td></td>
        <td>{{ topUnit.total }}</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>

      <template v-for="org in topUnit.organizations" :key="org.id">
        <template v-for="(counter, i) in org.counters" :key="counter.id">
          <tr v-on:click="alert('click!!!')">
            <td v-if="i===0" class="fit-content organization-name" :rowspan="org.counters.length">{{ org.name }}</td>
            <td>{{ counter.consumptionByMonth.startingReading?.reading }}</td>
            <td>{{ counter.consumptionByMonth.endingReading?.reading }}</td>
            <td>{{ counter.consumptionByMonth.readingDelta }}</td>
            <td>{{ counter.consumptionByMonth.consumption }}</td>
            <td>{{ counter.consumptionByMonth.continuousPowerFlow }}</td>
            <td>{{ counter.K }}</td>
            <td>{{ counter.sn }}</td>
            <td>{{ counter.comment }}</td>
          </tr>
        </template>

      </template>
    </template>

    </tbody>

  </table>
</template>

<script>
import "@/assets/bootstrap.min.css"
import "./EnergyDistributionTable.css"

export default {
  name: "EnergyDistributionTable",
  props: {
    energyDistributionData: {
      type: Object,
      required: true
    }
  },
  computed: {
    toplevelUnits() {
      return this.energyDistributionData?.toplevelUnits;
    }
  }
}
</script>
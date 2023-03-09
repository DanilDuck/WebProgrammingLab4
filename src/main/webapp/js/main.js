new Vue({
    el: '#main',
    data: {
        x: undefined,
        y: undefined,
        r: undefined,
        results: undefined
    },
    created: function () {
        this.clearFields()
        axios.get('api/main')
            .then(response => this.results = response.data)
            .catch(() => location.href = 'auth.html')
    },
    methods: {
        addResult: function (x, y, r, clicked) {
            if(!isCorrect(x,y,r)){
                return;
            }
            console.log("x: "+ x +" y: "+ y+" r: "+ r);
            axios.post('api/main', new URLSearchParams({
                x: x,
                y: y,
                r: r,
                clicked: clicked
            }))
                .then(response => this.results.push(response.data))
                .catch(error => retardError(error.response.data.message))
        },
        addResultFromForm: function () {
            const yString = parseFloat(this.y.toString().replace(',', '.'))
            if (isNaN(yString) || yString < -3 || yString > 5) {
                notifyError('Y [-5;3]')
                return
            }
            this.addResult(this.x, yString, this.r, false)
        },
        addResultFromPlot: function (event) {
            const rect = document.querySelector('svg').getBoundingClientRect()
            const x = ((event.clientX - rect.left) - 150) / 100 * this.r
            const y = (150 - (event.clientY - rect.top)) / 100 * this.r
            this.addResult(x, y, this.r, true)
        },
        clearResults: function () {
            axios.delete('api/main')
                .then(() => this.results = [])
                .catch(() => notifyError('Cant clear table'))
        },
        logout: function () {
            axios.delete('api/auth')
            location.href = 'auth.html'
        },
        clearFields: function () {
            this.x = '0'
            this.y = ''
            this.r = '1'
        }
    },
    filters: {
        moment: function (date) {
            return moment(date).format('DD.MM.yy HH:mm:ss');
        },
        success: function (success) {
            return success ? 'Hit' : 'Miss'
        }
    }
})
function isCorrect(x,y,r){
    return ((x >= -5 && x <= 3 && y >= -3 && y <= 5) &&
         r >= -5 && r <= 3);
}
function isOnPlot(x, y, r) {
    console.log(this.r)
    return (x <= r && x >= 0 && y >= 0 && y <= r / 2) ||
        (x <= 0 && x >= -r / 2 && y <= 0 && y >= -r && 2 * x + y >= -r) ||
        (x <= 0 && y >= 0 && x*x + y*y <= r/2*r/2)
}